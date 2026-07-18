package org.windy.windymixin.mixin.TomsStorage;

import com.tom.storagemod.network.DataPacket;
import com.tom.storagemod.network.NetworkHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.windy.windymixin.compat.TomsStorageTerminal.TomsStorageTerminalSyncFix;


@Mixin(NetworkHandler.class)
public class NetworkHandlerClientMixin {

    // ⚠️ 注意这里的 remap = false，因为 handleDataClient 是模组方法，不需要经过原版混淆映射
    @Inject(method = "handleDataClient", at = @At("HEAD"), cancellable = true, remap = false)
    private static void windymixin$routeTerminalDataToReadyScreen(DataPacket packet, IPayloadContext context, CallbackInfo ci) {
        TomsStorageTerminalSyncFix.handleClientData(packet, context);
        ci.cancel();
    }
}