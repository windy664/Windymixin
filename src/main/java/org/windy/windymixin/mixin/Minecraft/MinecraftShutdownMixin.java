package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.windy.windymixin.Windymixin;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Minecraft.class)
public abstract class MinecraftShutdownMixin {
    private static final AtomicBoolean BOOTSTRAPPED = new AtomicBoolean();
    private static final AtomicBoolean ARMED = new AtomicBoolean();
    private static final AtomicBoolean MONITOR_STARTED = new AtomicBoolean();
    private static final int DELAY_SECONDS = 15;
    private static volatile long haltAtNanos = Long.MAX_VALUE;

    private static void bootstrap() {
        if (!BOOTSTRAPPED.compareAndSet(false, true)) {
            return;
        }

        startHaltMonitor();

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> arm("jvm shutdown hook"), "Windymixin-Shutdown-Hook"));
            Windymixin.LOGGER.info("[Windymixin] Installed shutdown guard");
        } catch (IllegalStateException e) {
            Windymixin.LOGGER.warn("[Windymixin] Failed to install shutdown hook", e);
        }
    }

    private static void startHaltMonitor() {
        if (!MONITOR_STARTED.compareAndSet(false, true)) {
            return;
        }

        Thread monitor = new Thread(() -> {
            while (true) {
                long deadline = haltAtNanos;
                if (deadline != Long.MAX_VALUE && System.nanoTime() >= deadline) {
                    Windymixin.LOGGER.error("[Windymixin] Shutdown guard forcing JVM halt after {} seconds", DELAY_SECONDS);
                    Runtime.getRuntime().halt(0);
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {
                }
            }
        }, "Windymixin-Shutdown-Monitor");
        monitor.setDaemon(true);
        monitor.start();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void windymixin$installShutdownGuard(CallbackInfo ci) {
        bootstrap();
    }

    private static void arm(String trigger) {
        bootstrap();
        if (!ARMED.compareAndSet(false, true)) {
            return;
        }

        haltAtNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(DELAY_SECONDS);

        long pid = ProcessHandle.current().pid();
        try {
            ProcessBuilder builder = createProcessBuilder(pid);
            builder.redirectInput(ProcessBuilder.Redirect.DISCARD);
            builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            builder.redirectError(ProcessBuilder.Redirect.DISCARD);
            builder.start();
            Windymixin.LOGGER.info("[Windymixin] Armed shutdown guard for process {} ({})", pid, trigger);
        } catch (Exception e) {
            Windymixin.LOGGER.warn("[Windymixin] Failed to start external shutdown guard for process {} ({})", pid, trigger, e);
        }
    }

    private static ProcessBuilder createProcessBuilder(long pid) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return createWindowsProcessBuilder(pid);
        }

        return new ProcessBuilder(
              "sh",
              "-c",
              "sleep " + DELAY_SECONDS + "; kill -TERM " + pid + " 2>/dev/null; sleep 3; kill -KILL " + pid + " 2>/dev/null"
        );
    }

    private static ProcessBuilder createWindowsProcessBuilder(long pid) {
        String innerScript = "$watchPid = " + pid + "\n"
              + "Start-Sleep -Seconds " + DELAY_SECONDS + "\n"
              + "if (Get-Process -Id $watchPid -ErrorAction SilentlyContinue) { taskkill.exe /PID $watchPid /T /F | Out-Null }\n";
        String encodedInner = Base64.getEncoder().encodeToString(innerScript.getBytes(StandardCharsets.UTF_16LE));
        String outerScript = "Start-Process -WindowStyle Hidden powershell.exe "
              + "-ArgumentList @('-NoProfile','-ExecutionPolicy','Bypass','-EncodedCommand','" + encodedInner + "')";
        return new ProcessBuilder(
              "powershell.exe",
              "-NoProfile",
              "-ExecutionPolicy",
              "Bypass",
              "-WindowStyle",
              "Hidden",
              "-Command",
              outerScript
        );
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void windymixin$startShutdownProcessCleanerOnStop(CallbackInfo ci) {
        arm("client stop");
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void windymixin$startShutdownProcessCleanerOnClose(CallbackInfo ci) {
        arm("client close");
    }
}
