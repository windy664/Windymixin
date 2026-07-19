package org.windy.windymixin.mixin.Iris;

import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.irisshaders.iris.vertices.ImmediateState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 Iris Shaders 开启光影后 TextDisplay 悬浮字不显示的问题。
 *
 * Iris 替换了 TEXT 渲染管线（shader + 顶点格式），但 TextDisplay 的文本内容
 * 仍走 vanilla Font.drawInBatch() 路径，vanilla buffer 不提供 Iris 扩展顶点属性，
 * 导致 shader 读到垃圾数据 → 文字不可见，只剩投影。
 *
 * 修复：在 DisplayRenderer.submit() 前后：
 * 1. 关闭 Iris 的 isRenderingLevel（禁用顶点格式替换）
 * 2. 设置 bypass 标志（让 IrisRenderingPipeline.shouldOverrideShaders() 返回 false）
 *
 * @see IrisTextDisplayPipelineMixin
 * @see <a href="https://github.com/IrisShaders/Iris/issues/2756">Iris #2756</a>
 */
@Mixin(DisplayRenderer.class)
public class IrisTextDisplayMixin {

    @Unique
    static boolean WINDYMIXIN$BYPASS_DISPLAY = false;

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/DisplayEntityRenderState;"
            + "Lcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lnet/minecraft/client/renderer/SubmitNodeCollector;"
            + "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("HEAD")
    )
    private void windymixin$disableIrisForDisplay(CallbackInfo ci) {
        WINDYMIXIN$BYPASS_DISPLAY = true;
        ImmediateState.isRenderingLevel = false;
        ImmediateState.renderWithExtendedVertexFormat = false;
    }

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/DisplayEntityRenderState;"
            + "Lcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lnet/minecraft/client/renderer/SubmitNodeCollector;"
            + "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("RETURN")
    )
    private void windymixin$restoreIrisForDisplay(CallbackInfo ci) {
        WINDYMIXIN$BYPASS_DISPLAY = false;
        ImmediateState.isRenderingLevel = true;
        ImmediateState.renderWithExtendedVertexFormat = true;
    }
}
