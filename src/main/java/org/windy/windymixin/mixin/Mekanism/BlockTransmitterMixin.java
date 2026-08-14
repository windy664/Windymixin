package org.windy.windymixin.mixin.Mekanism;

import mekanism.common.block.transmitter.BlockTransmitter;
import mekanism.common.registries.MekanismItems;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockTransmitter.class, remap = false)
public class BlockTransmitterMixin {

    /**
     * 修复注册表烘焙阶段 CONFIGURATOR 未绑定导致的 NPE。
     * 当物品未绑定时，返回空气物品占位。
     */
    @Redirect(
            method = "getShape",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/registries/DeferredHolder;value()Ljava/lang/Object;"
            ),
            require = 1
    )
    private Object windymixin$guardConfiguratorValue(DeferredHolder<?, ?> holder) {
        if (holder == MekanismItems.CONFIGURATOR && !holder.isBound()) {
            return Items.AIR; // 未绑定时返回空气物品
        }
        return holder.value();
    }
}