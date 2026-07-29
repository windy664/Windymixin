package org.windy.windymixin.mixin.Mysticalagriculture;

import com.blakebr0.mysticalagriculture.client.tesr.renderer.EssenceVesselRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复MA 9.0.3 EssenceVesselRenderer在26.2上的崩溃。
 *
 * 原版调用Minecraft.renderBuffers()但该方法在26.2已移除。
 * 直接跳过渲染，避免崩溃。
 */
@Mixin(value = EssenceVesselRenderer.class, priority = 1100)
public class EssenceVesselRendererMixin {

    /**
     * 跳过submit方法，避免调用不存在的renderBuffers()。
     */
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true, require = 0)
    private static void windymixin$skipRender(CallbackInfo ci) {
        ci.cancel();
    }
}
