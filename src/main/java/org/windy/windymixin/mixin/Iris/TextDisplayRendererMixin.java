package org.windy.windymixin.mixin.Iris;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.util.FormattedCharSequence; // <-- 关键修改：引入 FormattedCharSequence
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DisplayRenderer.TextDisplayRenderer.class)
public class TextDisplayRendererMixin {
    @Redirect(
            method = "submitInner(Lnet/minecraft/client/renderer/entity/state/TextDisplayEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitText(Lcom/mojang/blaze3d/vertex/PoseStack;FFLnet/minecraft/util/FormattedCharSequence;ZLnet/minecraft/client/gui/Font$DisplayMode;IIII)V"
            )
    )
    private void fixIrisTextDisplayVisibility(OrderedSubmitNodeCollector instance, PoseStack poseStack, float x, float y, FormattedCharSequence text, boolean shadow, Font.DisplayMode displayMode, int lightCoords, int color, int i1, int i2) {
        poseStack.pushPose();

        if (displayMode == Font.DisplayMode.POLYGON_OFFSET) {
            displayMode = Font.DisplayMode.NORMAL;
            poseStack.translate(0.0F, 0.0F, -0.03F);
        }

        instance.submitText(poseStack, x, y, text, shadow, displayMode, lightCoords, color, i1, i2);

        poseStack.popPose();
    }
}