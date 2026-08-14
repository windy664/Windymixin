package org.windy.windymixin.mixin.Mekanism;

import mekanism.client.ClientTickHandler;
import mekanism.common.item.interfaces.IJetpackItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复喷气背包悬停模式客户端状态不同步的问题。
 */
@Mixin(value = ClientTickHandler.class, remap = false, priority = 1100)
public class JetpackHoverSyncMixin {

    @Shadow
    private static Minecraft minecraft;

    /**
     * 在方法头部注入，完全替换原方法逻辑
     */
    @Inject(
            method = "isJetpackInUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void windymixin$fixJetpackHoverCheck(
            LocalPlayer player,
            ItemAccess jetpack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // 保留原版的空值和观察者检查
        if (player.isSpectator() || jetpack == null) {
            cir.setReturnValue(false);
            return;
        }

        // 获取 ItemResource
        ItemResource jetpackType = jetpack.getResource();
        if (jetpackType.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        // 检查是否为 IJetpackItem 实例
        if (jetpackType.getItem() instanceof IJetpackItem jetpackItem) {
            IJetpackItem.JetpackMode mode = jetpackItem.getJetpackMode(jetpackType);
            boolean result = IJetpackItem.getPlayerJetpackMode(player, mode,
                    JetpackHoverSyncMixin::isAscending) != IJetpackItem.JetpackMode.DISABLED;
            cir.setReturnValue(result);
        } else {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean isAscending(LocalPlayer player) {
        return minecraft.gui.screen() == null && player.input.keyPresses.jump();
    }
}