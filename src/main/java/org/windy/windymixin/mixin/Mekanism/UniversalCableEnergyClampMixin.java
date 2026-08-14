package org.windy.windymixin.mixin.Mekanism;

import mekanism.common.content.network.transmitter.UniversalCable;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 修复 UniversalCable 能量共享超出网络总量导致负数能量的问题。
 *
 * 原版 takeShare 中 saveShare 可能大于网络实际能量，
 * 导致 setEnergy 传入负值，引发能量网络异常。
 * 通过 redirect setEnergy 调用，将值钳制到非负。
 *
 * @author Windy
 * @reason saveShare 未做上限检查导致能量为负
 */
@Mixin(value = UniversalCable.class, remap = false, priority = 1100)
public class UniversalCableEnergyClampMixin {

    /**
     * Redirect 能量容器的 setEnergy 调用，确保不会设置负数能量。
     * 在 takeShare 方法中，将 saveShare 钳制到网络实际能量。
     */
    @Redirect(
        method = "takeShare",
        at = @At(
            value = "INVOKE",
            target = "Lmekanism/api/energy/IEnergyContainer;setEnergy(JLnet/neoforged/neoforge/transfer/transaction/TransactionContext;)V"
        ),
        require = 0
    )
    private void windymixin$clampEnergy(
            mekanism.api.energy.IEnergyContainer container,
            long energy,
            TransactionContext transaction) {
        // 钳制：确保不会设置负数能量
        container.setEnergy(Math.max(0, energy), transaction);
    }
}
