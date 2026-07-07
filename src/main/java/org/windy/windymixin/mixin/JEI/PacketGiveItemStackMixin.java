package org.windy.windymixin.mixin.JEI;

import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PacketGiveItemStack;
import org.spongepowered.asm.mixin.Mixin;

/**
 * JEI创造物品：配合 PacketRequestCheatPermissionMixin（强制hasPermission=true），
 * process() 内 executeGive → hasPermissionForCheatMode 必定通过，所有物品均可拿取。
 * 无需额外拦截。
 */
@Mixin(PacketGiveItemStack.class)
public class PacketGiveItemStackMixin {
}
