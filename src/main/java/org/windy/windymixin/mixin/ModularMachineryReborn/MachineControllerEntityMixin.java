package org.windy.windymixin.mixin.ModularMachineryReborn;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.manager.ComponentManager;
import es.degrassi.mmreborn.common.manager.crafting.MachineProcessor;
import es.degrassi.mmreborn.common.manager.crafting.MachineStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import org.windy.windymixin.mixin.Minecraft.BlockEntityAccessor;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 🧠 MachineControllerEntity Elastic Adaptive Scheduler
 *
 * 修复版（兼容NeoForge/Mojang mappings）：
 * ✅ 修复 getLevel() 抽象方法不可解析问题 -> 改为 Shadow 字段
 * ✅ 修复 server.tickTimes 无法访问问题 -> 使用 getAverageTickTime() + 反射Fallback
 */
@Mixin(MachineControllerEntity.class)
public abstract class MachineControllerEntityMixin {

    // ========== 原字段 Shadow ==========
    @Shadow private long lastCheckTick;
    @Shadow private long tickOffset;
    @Shadow protected abstract ComponentManager getComponentManager();
    @Shadow protected abstract MachineProcessor getProcessor();
    @Shadow protected abstract MachineStatus getStatus();
    @Shadow protected abstract void setStatus(MachineStatus status);

    // ========== 弹性异步线程池 ==========
    @Unique
    private static final int CPU_CORES = Math.max(2, Runtime.getRuntime().availableProcessors());
    @Unique
    private static final AtomicInteger ACTIVE_THREADS = new AtomicInteger(2);
    @Unique
    private static final ThreadPoolExecutor ELASTIC_POOL =
            new ThreadPoolExecutor(
                    2, CPU_CORES,
                    30L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1024),
                    new ThreadFactoryBuilder().setNameFormat("MMR-Elastic-%d").build(),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    // ========== 动态调度全局变量 ==========
    @Unique private static long lastTpsCheckTime = 0L;
    @Unique private static double tpsEstimate = 20.0;
    @Unique private static int smoothedLoad = 0;

    @Unique private static long STRUCTURE_INTERVAL = 100L;
    @Unique private static long COMPONENT_INTERVAL = 40L;
    @Unique private static long PROCESS_INTERVAL = 10L;

    @Unique private long lastComponentTick;
    @Unique private long lastProcessTick;
    @Unique private Future<?> structureFuture;
    @Unique private Future<?> componentFuture;
    @Unique private Future<?> processorFuture;
    @Unique private boolean scheduledStructure, scheduledComponent, scheduledProcess;

    // ======================================================

    /**
     * ✅ 主tick拦截方法
     */
    @Inject(method = "doRestrictedTick", at = @At("HEAD"), cancellable = true)
    private void onRestrictedTick(CallbackInfo ci) {
        // ✅ 用字段 level 替代 getLevel()
        Level lvl = ((BlockEntityAccessor) this).getLevelOrWorld();
        if (!(lvl instanceof ServerLevel server)) return;
        long tick = lvl.getGameTime();

        // TPS采样 + 动态线程调节
        adaptToServerTPS(server);

        if (!scheduledStructure && tick - lastCheckTick > STRUCTURE_INTERVAL + tickOffset % 10)
            scheduleStructure(server);

        if (!scheduledComponent && tick - lastComponentTick > COMPONENT_INTERVAL)
            scheduleComponent(server);

        if (!scheduledProcess && tick - lastProcessTick > PROCESS_INTERVAL)
            scheduleProcessor(server);

        applyResultSafe();
        ci.cancel();
    }

    // ======================================================
    // ⚙️ 动态TPS处理 + 线程池弹性调整
    // ======================================================

    @Unique
    private void adaptToServerTPS(ServerLevel level) {
        if (System.currentTimeMillis() - lastTpsCheckTime < 2000) return;
        lastTpsCheckTime = System.currentTimeMillis();

        MinecraftServer server = level.getServer();

        double meanTickTime = getSafeAverageTickTime(server);
        tpsEstimate = Math.min(1000.0 / Math.max(50.0, meanTickTime), 20.0);

        double tps = tpsEstimate;
        int loadQ = ELASTIC_POOL.getQueue().size();

        smoothedLoad = (smoothedLoad * 3 + loadQ) / 4;

        if (tps >= 19.8) {
            resizePool(CPU_CORES);
            STRUCTURE_INTERVAL = 100;
            COMPONENT_INTERVAL = 40;
            PROCESS_INTERVAL = 10;
        } else if (tps >= 18.0) {
            resizePool(Math.max(2, CPU_CORES / 2));
            STRUCTURE_INTERVAL = 200;
            COMPONENT_INTERVAL = 80;
            PROCESS_INTERVAL = 20;
        } else if (tps >= 15.0) {
            resizePool(2);
            STRUCTURE_INTERVAL = 400;
            COMPONENT_INTERVAL = 100;
            PROCESS_INTERVAL = 30;
        } else {
            resizePool(1);
            STRUCTURE_INTERVAL = 800;
            COMPONENT_INTERVAL = 200;
            PROCESS_INTERVAL = 60;
        }

        if (level.getGameTime() % 200 == 0) {
            ModularMachineryReborn.LOGGER.info(
                    String.format("[ElasticScheduler] TPS=%.2f | Threads=%d | Queue=%d | Structure=%dt | Components=%dt | Processor=%dt",
                            tps, ACTIVE_THREADS.get(), smoothedLoad, STRUCTURE_INTERVAL, COMPONENT_INTERVAL, PROCESS_INTERVAL)
            );
        }
    }

    // ✅ 安全获取平均Tick时间（全版本兼容）
    @Unique
    private double getSafeAverageTickTime(MinecraftServer server) {
        try {
            // Forge / Mojang 混编支持：直接读取 tickTimes 数组
            java.lang.reflect.Field field = server.getClass().getDeclaredField("tickTimes");
            field.setAccessible(true);
            long[] tickArray = (long[]) field.get(server);
            if (tickArray != null && tickArray.length > 0) {
                return Arrays.stream(tickArray).average().orElse(50000d) / 1_000_000d;
            }
        } catch (Throwable ignored) { }

        // 兼容 Fabric / Yarn 环境（1.20.3+/Paper）
        try {
            var method = server.getClass().getDeclaredMethod("getAverageTickTime");
            method.setAccessible(true);
            Object result = method.invoke(server);
            if (result instanceof Number n)
                return n.doubleValue();
        } catch (Throwable ignored) { }

        // Fallback 默认 50ms
        return 50.0;
    }

    // ======================================================
    // ⚙️ 线程池调整
    // ======================================================

    @Unique
    private void resizePool(int targetThreads) {
        if (targetThreads == ACTIVE_THREADS.get()) return;
        ELASTIC_POOL.setCorePoolSize(targetThreads);
        ELASTIC_POOL.setMaximumPoolSize(targetThreads);
        ACTIVE_THREADS.set(targetThreads);
    }

    // ======================================================
    // ⚙️ 异步任务调度
    // ======================================================

    @Unique
    private void scheduleStructure(ServerLevel level) {
        scheduledStructure = true;
        structureFuture = ELASTIC_POOL.submit(() -> {
            try {
                MachineControllerEntity self = (MachineControllerEntity) (Object) this;
                boolean valid = self.getFoundMachine().getPattern().match(level,
                        self.getBlockPos(),
                        self.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING));
                if (!valid && !self.getStatus().isMissingStructure()) {
                    self.setStatus(MachineStatus.MISSING_STRUCTURE);
                } else if (valid && self.getStatus().isMissingStructure()) {
                    self.setStatus(MachineStatus.IDLE);
                }
            } catch (Exception e) {
                ModularMachineryReborn.LOGGER.error("[ElasticScheduler] 异步结构检测错误", e);
            } finally {
                scheduledStructure = false;
                lastCheckTick = level.getGameTime();
            }
        });
    }

    @Unique
    private void scheduleComponent(ServerLevel level) {
        scheduledComponent = true;
        lastComponentTick = level.getGameTime();
        componentFuture = ELASTIC_POOL.submit(() -> {
            try {
                MachineControllerEntity self = (MachineControllerEntity) (Object) this;
                self.getComponentManager().updateComponents(false);
            } catch (Exception e) {
                ModularMachineryReborn.LOGGER.error("[ElasticScheduler] 异步组件更新异常", e);
            } finally {
                scheduledComponent = false;
            }
        });
    }

    @Unique
    private void scheduleProcessor(ServerLevel level) {
        scheduledProcess = true;
        lastProcessTick = level.getGameTime();
        processorFuture = ELASTIC_POOL.submit(() -> {
            try {
                MachineControllerEntity self = (MachineControllerEntity) (Object) this;
                if (!self.isPaused()) {
                    self.getProcessor().tick();
                }
            } catch (Exception e) {
                ModularMachineryReborn.LOGGER.error("[ElasticScheduler] 异步Processor异常", e);
            } finally {
                scheduledProcess = false;
            }
        });
    }

    // ======================================================
    // ⚙️ 执行结果清理
    // ======================================================

    @Unique
    private void applyResultSafe() {
        checkFuture(structureFuture);
        checkFuture(componentFuture);
        checkFuture(processorFuture);
    }

    @Unique
    private void checkFuture(Future<?> future) {
        if (future == null || !future.isDone()) return;
        try {
            future.get(5, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ignored) {
        } catch (Exception e) {
            ModularMachineryReborn.LOGGER.warn("[ElasticScheduler] 任务结果异常", e);
        }
    }

    // ======================================================
    // ⚙️ 释放资源
    // ======================================================

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void shutdownPool(CallbackInfo ci) {
        if (!ELASTIC_POOL.isShutdown()) {
            ELASTIC_POOL.shutdown();
        }
    }


}