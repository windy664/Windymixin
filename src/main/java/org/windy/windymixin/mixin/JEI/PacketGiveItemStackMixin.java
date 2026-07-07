package org.windy.windymixin.mixin.JEI;

import mezz.jei.common.config.GiveMode;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PacketGiveItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * JEI创造物品：跳过权限检查，所有物品均可拿取。
 * 配合 PacketRequestCheatPermissionMixin（客户端强制开启作弊模式）使用。
 */
@Mixin(PacketGiveItemStack.class)
public class PacketGiveItemStackMixin {

    @Shadow
    private ItemStack itemStack;
    @Shadow
    private GiveMode giveMode;

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void windymixin$bypassPermissionCheck(ServerPacketContext context, CallbackInfo ci) {
        if (itemStack.isEmpty()) {
            return;
        }
        ServerPlayer player = context.player();
        if (giveMode == GiveMode.INVENTORY) {
            player.getInventory().placeItemBackInInventory(itemStack.copy());
        } else if (giveMode == GiveMode.MOUSE_PICKUP) {
            player.containerMenu.setCarried(itemStack.copy());
        }
        ci.cancel();
    }
}
