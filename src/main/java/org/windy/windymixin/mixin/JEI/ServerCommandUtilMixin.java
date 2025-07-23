package org.windy.windymixin.mixin.JEI;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mezz.jei.common.config.GiveMode;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.util.ServerCommandUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandUtil.class)
public class ServerCommandUtilMixin {

    @Inject(
            method = "executeGive",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onExecuteGive(ServerPacketContext context, ItemStack itemStack, GiveMode giveMode, CallbackInfo ci) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());

        if (id == null || !"yuushya".equals(id.getNamespace())) {
            ServerPlayer sender = context.player();
            sender.sendSystemMessage(Component.literal("§cJEI只能免费获取 Yuushya 的方块！"));
            ci.cancel();
        }
    }

    /**
     * 修改 hasPermissionForCheatMode 方法的返回值，当原本返回 false 时改为 true。
     */
    @ModifyReturnValue(
            method = "hasPermissionForCheatMode(Lnet/minecraft/world/entity/player/Player;Lmezz/jei/common/config/IServerConfig;)Z",
            at = @At("RETURN")
    )
    private static boolean onHasPermissionForCheatModeReturn(boolean original) {
        return original || true; // 永远返回 true
    }
}
