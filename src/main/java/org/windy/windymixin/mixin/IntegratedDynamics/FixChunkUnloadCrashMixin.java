package org.windy.windymixin.mixin.IntegratedDynamics;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.cyclops.integrateddynamics.api.network.INetworkCarrier;
import org.cyclops.integrateddynamics.api.path.IPathElement;
import org.cyclops.integrateddynamics.core.blockentity.BlockEntityMultipartTicking;
import org.cyclops.integrateddynamics.core.helper.CableHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 修复 Integrated Dynamics 区块卸载崩溃。
 *
 * setRemoved → CableHelpers.onCableRemovingNetwork(BlockState, BlockEntity, INetworkCarrier, IPathElement)
 * → ... → syncLoad → IllegalStateException
 *
 * 区块正在卸载时，安全忽略此异常。
 */
@Mixin(value = BlockEntityMultipartTicking.class, remap = false)
public class FixChunkUnloadCrashMixin {

    @Redirect(
        method = "setRemoved",
        at = @At(value = "INVOKE", target = "Lorg/cyclops/integrateddynamics/core/helper/CableHelpers;onCableRemovingNetwork(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lorg/cyclops/integrateddynamics/api/network/INetworkCarrier;Lorg/cyclops/integrateddynamics/api/path/IPathElement;)Z")
    )
    private boolean windymixin$safeOnCableRemovingNetwork(BlockState blockState, BlockEntity blockEntity, INetworkCarrier carrier, IPathElement pathElement) {
        try {
            return CableHelpers.onCableRemovingNetwork(blockState, blockEntity, carrier, pathElement);
        } catch (IllegalStateException e) {
            // 区块正在卸载，syncLoad 被禁止，安全忽略
            return false;
        }
    }
}
