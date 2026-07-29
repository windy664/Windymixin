package org.windy.windymixin.mixin.EnderIO;

import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitBaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复EnderIO ConduitBaker与Sodium多线程的冲突。
 *
 * 简化方案：在构造器HEAD处检查线程，如果是工作线程就跳过初始化。
 * 代价：管道在工作线程渲染时不显示，但不会崩。
 */
@Mixin(value = ConduitBaker.class, priority = 1100)
public class ConduitBakerMixin {

    /**
     * 如果当前线程是Sodium的工作线程（非Render线程），跳过构造器。
     * 这样可以避免HashMap多线程冲突。
     */
    @Inject(method = "<init>", at = @At("HEAD"), cancellable = true, require = 0)
    private void windymixin$skipIfWorkerThread(CallbackInfo ci) {
        String threadName = Thread.currentThread().getName();
        // Sodium工作线程通常叫 "Chunk Builder #" 或 "Worker-"
        if (threadName.contains("Chunk Builder") || threadName.contains("Worker-")) {
            ci.cancel();
        }
    }
}
