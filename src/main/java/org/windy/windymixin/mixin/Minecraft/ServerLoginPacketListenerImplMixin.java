package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListenerImplMixin {
    @Shadow
    private int tick;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    @SuppressWarnings({"cast", "ConstantConditions"})
    private void tick(CallbackInfo info) {
        if (this.tick > 6000) {
            ServerLoginPacketListenerImpl underlying = ((ServerLoginPacketListenerImpl) (Object) this);
            underlying.disconnect(Component.nullToEmpty("恭喜你，连接超时，你的网络，真差劲。"));
        }
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/server/network/ServerLoginPacketListenerImpl.disconnect(Lnet/minecraft/network/chat/Component;)V"
            )
    )
    private void disconnect(ServerLoginPacketListenerImpl packetListener, Component reason) {

    }

}