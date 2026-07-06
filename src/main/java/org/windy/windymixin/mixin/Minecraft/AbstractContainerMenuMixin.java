package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.windy.windymixin.Windymixin;

import java.util.List;

/**
 * 防炸容器：若 {@code initializeContents} 收到的 items 数量超过实际槽位数，
 * 原版会越界/异常导致崩服。这里截断到槽位数量后手动填充，安全返回。
 *
 * <p>26.2 签名：{@code initializeContents(int stateId, List<ItemStack> items, ItemStack carried)}。</p>
 */
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Inject(method = "initializeContents", at = @At("HEAD"), cancellable = true)
    private void windymixin$guardInitContents(int stateId, List<ItemStack> items, ItemStack carried, CallbackInfo ci) {
        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        int slotSize = self.slots.size();
        if (items.size() > slotSize) {
            Windymixin.LOGGER.warn("[防炸] 收到非法容器同步包 items.size={} > slots.size={}，已截断。", items.size(), slotSize);
            for (int i = 0; i < slotSize; ++i) {
                self.getSlot(i).set(items.get(i));
            }
            self.setCarried(carried);
            ci.cancel();
        }
    }
}
