package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    // save(HolderLookup.Provider, Tag)
    @Inject(
            method = "save(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;",
            at = @At("HEAD")
    )
    private void sanitizeCountBeforeSave1(HolderLookup.Provider levelRegistryAccess, Tag outputTag, CallbackInfoReturnable<Tag> cir) {
        sanitize((ItemStack)(Object)this);
    }

    // save(HolderLookup.Provider)
    @Inject(
            method = "save(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/Tag;",
            at = @At("HEAD")
    )
    private void sanitizeCountBeforeSave2(HolderLookup.Provider levelRegistryAccess, CallbackInfoReturnable<Tag> cir) {
        sanitize((ItemStack)(Object)this);
    }

    /**
     * 如果 count 非法，直接设为1（
     */
    private static void sanitize(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        int count = stack.getCount();
        int max = stack.getMaxStackSize();
        if (count < 1 || count > max) {
            // 避免保存时崩溃
            stack.setCount(1);
        }
    }
}