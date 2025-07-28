package org.windy.windymixin.mixin.Minecraft;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Inject(
            method = "initializeContents",
            at = @At("HEAD"),
            cancellable = true
    )
    private void fixInitContents(int containerId, List<ItemStack> items, ItemStack carried, CallbackInfo ci) {
        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        int slotSize = self.slots.size();
        if (items.size() > slotSize) {
            System.out.println("[防炸] 收到非法同步包，items.size=" + items.size() + " > slots.size=" + slotSize);
            // 拷贝前 slotSize 个元素，避免 subList 视图问题
            List<ItemStack> fixed = new ArrayList<>(slotSize);
            for (int i = 0; i < slotSize; ++i) {
                fixed.add(items.get(i));
            }
            // 直接修正 slots
            for (int i = 0; i < fixed.size(); ++i) {
                self.getSlot(i).set(fixed.get(i));
            }
            self.setCarried(carried);
            // 你可能还要修正 stateId 如有同步包带上
            ci.cancel();
        }
    }
}