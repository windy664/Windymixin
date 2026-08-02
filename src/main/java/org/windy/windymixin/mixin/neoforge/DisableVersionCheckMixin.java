package org.windy.windymixin.mixin.neoforge;

import net.neoforged.fml.VersionChecker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 禁用 NeoForge 模组版本检查，启动时不再逐个联网查更新。
 */
@Mixin(VersionChecker.class)
public class DisableVersionCheckMixin {

    @Inject(method = "startVersionCheck", at = @At("HEAD"), cancellable = true)
    private static void windymixin$disableVersionCheck(CallbackInfo ci) {
        ci.cancel();
    }
}
