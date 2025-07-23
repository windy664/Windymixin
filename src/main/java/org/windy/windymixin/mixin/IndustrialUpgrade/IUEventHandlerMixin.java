package org.windy.windymixin.mixin.IndustrialUpgrade;



import com.denfop.events.IUEventHandler;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IUEventHandler.class)
public class IUEventHandlerMixin {
    /**
     * 防止 tupleReplicatorRecipe 为 null 时 addInfo 方法崩溃
     */
    @Inject(method = "addInfo", at = @At("HEAD"), cancellable = true)
    private void windymixin$preventNullTupleReplicatorRecipe(ItemTooltipEvent event, CallbackInfo ci) {
        try {
            // 反射拿到 this.tupleReplicatorRecipe
            Object tuple = this.getClass().getDeclaredField("tupleReplicatorRecipe").get(this);
            if (tuple == null) {
                ci.cancel(); // 直接跳过后续 addInfo 逻辑
            }
        } catch (Throwable e) {
            // 反射失败也跳过，最大限度防崩
            ci.cancel();
        }
    }
}