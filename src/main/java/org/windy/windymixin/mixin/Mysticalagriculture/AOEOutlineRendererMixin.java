package org.windy.windymixin.mixin.Mysticalagriculture;

import com.blakebr0.mysticalagriculture.api.tinkering.AOEAugment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes; // 使用正确的 RenderTypes
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "com.blakebr0.Mysticalagriculture.client.handler.AOEAugmentClientHandler$AOEOutlineRenderer")
public abstract class AOEOutlineRendererMixin {

    @Shadow @Final private Player player;
    @Shadow @Final private ItemStack stack;
    @Shadow @Final private int range;
    @Shadow @Final private BlockHitResult hitResult;

    /**
     * 重写神秘农业中缺失的新版 CustomBlockOutlineRenderer.render 方法
     */
    public boolean render(BlockOutlineRenderState renderState, SubmitNodeCollector submitNodeCollector, PoseStack poseStack, LevelRenderState levelRenderState) {
        float red = this.player.isCrouching() ? 1.0F : 0.0F;
        float green = this.player.isCrouching() ? 0.5F : 1.0F;
        float blue = 0.0F;
        float alpha = 0.7F;

        Direction direction = this.player.isCrouching() ? Direction.UP : this.hitResult.getDirection();
        var camPos = levelRenderState.cameraRenderState.pos;

        // 1. 使用 RenderTypes.LINES 传入类型
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, vertexConsumer) -> {
            Matrix4f matrix = pose.pose();

            AOEAugment.getAOEBlocks(this.stack, this.range, this.hitResult.getBlockPos(), direction, this.player).forEach(aoePos -> {
                var state = this.player.level().getBlockState(aoePos);
                if (state.isAir())
                    return;

                double minX = aoePos.getX() - camPos.x;
                double minY = aoePos.getY() - camPos.y;
                double minZ = aoePos.getZ() - camPos.z;
                double maxX = minX + 1.0;
                double maxY = minY + 1.0;
                double maxZ = minZ + 1.0;

                // 2. 自定义画 12 条线框（完美替代缺失的 renderLineBox 方法）
                renderBoxLines(matrix, vertexConsumer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
            });
        });

        return true;
    }

    /**
     * 辅助绘制 3D 选框边线的工具方法
     */
    private static void renderBoxLines(Matrix4f matrix, VertexConsumer buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        float x1 = (float) minX, y1 = (float) minY, z1 = (float) minZ;
        float x2 = (float) maxX, y2 = (float) maxY, z2 = (float) maxZ;

        // 底面 4 条边
        line(matrix, buffer, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(matrix, buffer, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(matrix, buffer, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(matrix, buffer, x1, y1, z2, x1, y1, z1, r, g, b, a);

        // 顶面 4 条边
        line(matrix, buffer, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(matrix, buffer, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(matrix, buffer, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(matrix, buffer, x1, y2, z2, x1, y2, z1, r, g, b, a);

        // 4 条垂直柱线
        line(matrix, buffer, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(matrix, buffer, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(matrix, buffer, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(matrix, buffer, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private static void line(Matrix4f matrix, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        buffer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a);
        buffer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a);
    }
}