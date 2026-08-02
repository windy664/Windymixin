package org.windy.windymixin.mixin.Minecraft;

import com.mojang.realmsclient.client.RealmsClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

/**
 * 禁用 Realms feature flags 获取，避免启动时联网卡住。
 */
@Mixin(RealmsClient.class)
public class DisableRealmsMixin {

    @Inject(method = "fetchFeatureFlags", at = @At("HEAD"), cancellable = true)
    private void windymixin$disableFetchFeatureFlags(CallbackInfoReturnable<Set<String>> cir) {
        cir.setReturnValue(Set.of());
    }
}
