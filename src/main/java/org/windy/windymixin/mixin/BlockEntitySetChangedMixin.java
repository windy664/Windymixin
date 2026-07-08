package org.windy.windymixin.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 Productive Bees 等 mod 的蜜蜂 tick 时因跨区块 setChanged() 导致的主线程死锁。
 *
 * <p>原版 {@link BlockEntity#setChanged()} 会调用 {@code level.updateNeighbourForOutputSignal(pos)}，
 * 该方法内部调用 {@code level.getBlockState()}，如果邻近区块未加载，会触发同步区块加载，
 * 在某些场景下（如 AE2 生成陨石时）导致主线程阻塞死锁，超 60 秒触发看门狗崩溃。
 *
 * <p>修复方案：在调用 updateNeighbourForOutputSignal 之前，检查目标坐标的区块是否已加载。
 * 未加载则跳过方块更新通知（不影响存档，只影响红石信号更新）。
 */
@Mixin(BlockEntity.class)
public abstract class BlockEntitySetChangedMixin {

    @Shadow(remap = false)
    protected Level level;

    @Shadow(remap = false)
    protected BlockPos worldPosition;

    /**
     * 注入到 setChanged() 的头部，在 updateNeighbourForOutputSignal 调用之前。
     * 如果区块未加载，提前 return 跳过整个 setChanged 逻辑。
     */
    @Inject(method = "setChanged", at = @At("HEAD"), cancellable = true)
    private void windymixin$skipUpdateIfChunkUnloaded(CallbackInfo ci) {
        if (this.level != null && !this.level.isLoaded(this.worldPosition)) {
            // 区块未加载：跳过 setChanged()，避免触发同步区块加载导致死锁
            // 这只影响红石信号更新，不影响方块实体数据保存（save() 独立调用）
            ci.cancel();
        }
    }
}
