package org.windy.windymixin.mixin.Iris;

import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 配合 IrisTextDisplayMixin，在 Display Entity 渲染期间禁用 Iris shader 覆盖。
 *
 * IrisRenderingPipeline.shouldOverrideShaders() 检查 isRenderingWorld && isMainBound，
 * 不受 ImmediateState.isRenderingLevel 控制。需要单独注入让它在 Display 渲染期间返回 false。
 */
@Mixin(IrisRenderingPipeline.class)
public class IrisTextDisplayPipelineMixin {

    @Inject(method = "shouldOverrideShaders", at = @At("HEAD"), cancellable = true)
    private void windymixin$disableOverrideForDisplay(CallbackInfoReturnable<Boolean> cir) {
        if (IrisTextDisplayMixin.WINDYMIXIN$BYPASS_DISPLAY) {
            cir.setReturnValue(false);
        }
    }
}
