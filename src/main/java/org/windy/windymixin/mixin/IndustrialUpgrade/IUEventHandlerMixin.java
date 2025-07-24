package org.windy.windymixin.mixin.IndustrialUpgrade;

import net.minecraft.util.Tuple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.denfop.events.IUEventHandler;

@Mixin(IUEventHandler.class)
public class IUEventHandlerMixin {

    /**
     * 精准防御所有 addInfo 里的 Tuple.getA() 调用，防止 tupleReplicatorRecipe 为 null 崩溃
     */
    @Redirect(
            method = "addInfo(Lnet/neoforged/neoforge/event/entity/player/ItemTooltipEvent;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Tuple;getA()Ljava/lang/Object;"
            )
    )
    private Object windymixin$redirectGetA(Tuple<?, ?> tuple) {
        if (tuple == null) {
            return null; // 或 ItemStack.EMPTY，看mod兼容性
        }
        return tuple.getA();
    }

    @Redirect(
            method = "addInfo(Lnet/neoforged/neoforge/event/entity/player/ItemTooltipEvent;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Tuple;getB()Ljava/lang/Object;"
            )
    )
    private Object windymixin$redirectGetB(Tuple<?, ?> tuple) {
        if (tuple == null) {
            return null;
        }
        return tuple.getB();
    }
}