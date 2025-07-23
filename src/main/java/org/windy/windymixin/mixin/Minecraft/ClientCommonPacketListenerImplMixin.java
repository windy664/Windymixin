package org.windy.windymixin.mixin.Minecraft;


import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ClientCommonPacketListenerImplMixin {
    @Inject(
            method = "handleDisconnect",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onHandleDisconnect(ClientboundDisconnectPacket packet, CallbackInfo ci) {
        Component reason = packet.reason();
        if (reason.getString().contains("network.registries.sync") || reason.getString().contains("handshake failed") || reason.getString().contains("注册表") || reason.getString().contains("neoforge.network.data_maps.missing_our") ) {
            ci.cancel();
        }
    }
}