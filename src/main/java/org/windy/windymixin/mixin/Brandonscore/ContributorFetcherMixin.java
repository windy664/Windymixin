package org.windy.windymixin.mixin.Brandonscore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = com.brandon3055.brandonscore.handlers.contributor.ContributorFetcher.class, remap = false)
public abstract class ContributorFetcherMixin {
    @Inject(method = {"init", "fetchContributorFlags", "reload", "linkUser"}, at = @At("HEAD"), cancellable = true)
    private void disableNetworking(CallbackInfo ci) {
        System.out.println("[WindyMixin] 已阻止Brandonscore联网获取名单！为服务器启动速度又加快一步~");
        ci.cancel();
    }
}