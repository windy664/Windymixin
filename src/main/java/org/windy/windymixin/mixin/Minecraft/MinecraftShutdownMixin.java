package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.windy.windymixin.ShutdownWatchdog;

@Mixin(Minecraft.class)
public abstract class MinecraftShutdownMixin {

    @Inject(method = "stop", at = @At("HEAD"))
    private void windymixin$startShutdownProcessCleanerOnStop(CallbackInfo ci) {
        ShutdownWatchdog.start("client stop");
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void windymixin$startShutdownProcessCleanerOnClose(CallbackInfo ci) {
        ShutdownWatchdog.start("client close");
    }
}
