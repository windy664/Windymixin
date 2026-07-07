package org.windy.windymixin.mixin.JEI;

import mezz.jei.common.config.IServerConfig;
import mezz.jei.common.network.IConnectionToClient;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PacketCheatPermission;
import mezz.jei.common.network.packets.PacketRequestCheatPermission;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 强制所有玩家拥有JEI作弊模式权限。
 * 拦截客户端的权限查询请求，直接回复 hasPermission=true。
 */
@Mixin(PacketRequestCheatPermission.class)
public class PacketRequestCheatPermissionMixin {

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void windymixin$forceCheatPermission(ServerPacketContext context, CallbackInfo ci) {
        ServerPlayer player = context.player();
        IServerConfig serverConfig = context.serverConfig();
        IConnectionToClient connection = context.connection();
        connection.sendPacketToClient(new PacketCheatPermission(true, serverConfig), player);
        ci.cancel();
    }
}
