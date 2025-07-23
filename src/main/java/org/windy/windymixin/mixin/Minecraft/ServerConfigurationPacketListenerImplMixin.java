package org.windy.windymixin.mixin.Minecraft;


import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public class ServerConfigurationPacketListenerImplMixin {

    @Inject(
            method = "startNextTask",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onStartNextTask(CallbackInfo ci) {
        ServerConfigurationPacketListenerImpl self = (ServerConfigurationPacketListenerImpl)(Object)this;
        // 通过反射或者accessor获取 currentTask
        try {
            java.lang.reflect.Field f = ServerConfigurationPacketListenerImpl.class.getDeclaredField("currentTask");
            f.setAccessible(true);
            Object task = f.get(self);
            if (task != null) {
                System.err.println("[Mixin] Task has not finished yet, skip startNextTask this tick.");
                ci.cancel(); // 跳过原方法体
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}