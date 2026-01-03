package org.windy.windymixin.mixin.IndustrialUpgrade;

import com.denfop.proxy.ClientProxy;
import com.denfop.items.upgradekit.ItemUpgradeMachinesKit;
import com.denfop.IUItem;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Mixin(ClientProxy.class)
public class ClientProxyMixin {

    @Inject(method = "onServerTick", at = @At("HEAD"), cancellable = true)
    private void injectFixNullUpgradeList(ClientTickEvent.Pre event, CallbackInfo ci) {
        for (int i = 0; i < ItemUpgradeMachinesKit.inform.length; ++i) {
            List<ItemStack> list1 = IUItem.map_upgrades.get(i);
            if (list1 == null || list1.isEmpty()) continue; // 加入 null 判断
            ItemUpgradeMachinesKit.inform[i] = ++ItemUpgradeMachinesKit.inform[i] % list1.size();
        }


        ci.cancel();
    }
}