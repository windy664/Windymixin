package org.windy.windymixin.mixin.Minecraft;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.windy.windymixin.Windymixin;

@Mixin(Minecraft.class)
public abstract class MinecraftShutdownMixin {

    private static final AtomicBoolean WINDYMIXIN$CLEANER_STARTED = new AtomicBoolean();
    private static final int WINDYMIXIN$DELAY_SECONDS = 15;

    @Inject(method = "destroy", at = @At("HEAD"))
    private void windymixin$startShutdownProcessCleaner(CallbackInfo ci) {
        if (!WINDYMIXIN$CLEANER_STARTED.compareAndSet(false, true)) {
            return;
        }
        long pid = ProcessHandle.current().pid();
        try {
            ProcessBuilder builder = windymixin$createProcessBuilder(pid);
            builder.redirectInput(ProcessBuilder.Redirect.DISCARD);
            builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            builder.redirectError(ProcessBuilder.Redirect.DISCARD);
            builder.start();
            Windymixin.LOGGER.info("[Windymixin] Started shutdown watchdog for Minecraft process {}", pid);
        } catch (Exception e) {
            Windymixin.LOGGER.warn("[Windymixin] Failed to start shutdown watchdog for Minecraft process {}", pid, e);
        }
    }

    private static ProcessBuilder windymixin$createProcessBuilder(long pid) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return windymixin$createWindowsProcessBuilder(pid);
        }
        return new ProcessBuilder("sh", "-c", "sleep " + WINDYMIXIN$DELAY_SECONDS + "; kill -TERM " + pid + " 2>/dev/null; sleep 3; kill -KILL " + pid + " 2>/dev/null");
    }

    private static ProcessBuilder windymixin$createWindowsProcessBuilder(long pid) {
        String script = "$watchPid = " + pid + "\n" +
              "$selfPid = $PID\n" +
              "function Stop-Tree([int]$id) {\n" +
              "  $children = Get-CimInstance Win32_Process -Filter \"ParentProcessId = $id\" -ErrorAction SilentlyContinue | Where-Object { $_.ProcessId -ne $selfPid }\n" +
              "  foreach ($child in $children) { Stop-Tree ([int]$child.ProcessId) }\n" +
              "  if ($id -ne $selfPid) { Stop-Process -Id $id -Force -ErrorAction SilentlyContinue }\n" +
              "}\n" +
              "Start-Sleep -Seconds " + WINDYMIXIN$DELAY_SECONDS + "\n" +
              "if (Get-Process -Id $watchPid -ErrorAction SilentlyContinue) { Stop-Tree $watchPid }\n";
        String encoded = Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE));
        return new ProcessBuilder("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden", "-EncodedCommand", encoded);
    }
}
