package org.windy.windymixin.mixin.JEI;

import mezz.jei.common.config.GiveMode;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PacketGiveItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.windy.windymixin.Windymixin;

/**
 * JEI创造物品拦截：方块小镇(yuushya)和绀碧(ultramarine)建筑材料绕过权限检查无限拿取。
 * 在 PacketGiveItemStack.process() 头部拦截，命中则直接发放物品并取消原方法。
 */
@Mixin(PacketGiveItemStack.class)
public class PacketGiveItemStackMixin {

    @Shadow
    private ItemStack itemStack;
    @Shadow
    private GiveMode giveMode;

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void windymixin$allowYuushyaItems(ServerPacketContext context, CallbackInfo ci) {
        if (itemStack.isEmpty()) {
            return;
        }
        String key = itemStack.getItem().getDescriptionId();
        if (key.contains("yuushya") || key.contains("ultramarine")) {
            ServerPlayer player = context.player();
            Windymixin.LOGGER.info("[JEI无限拿] {} 拿取 {}", player.getName().getString(), itemStack);
            if (giveMode == GiveMode.INVENTORY) {
                player.getInventory().placeItemBackInInventory(itemStack.copy());
            } else if (giveMode == GiveMode.MOUSE_PICKUP) {
                player.containerMenu.setCarried(itemStack.copy());
            }
            ci.cancel();
        }
    }
}
