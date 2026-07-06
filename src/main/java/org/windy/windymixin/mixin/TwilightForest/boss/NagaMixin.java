package org.windy.windymixin.mixin.TwilightForest.boss;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import twilightforest.entity.boss.Naga;

@Mixin(value = Naga.class, remap = false) // 如果在开发环境下报错，尝试切换 remap
public class NagaMixin {

    /**
     * 解决 "No possible signatures" 和 "Cannot resolve target"
     * 目标方法签名必须完全匹配：net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
     */
    @Redirect(
            method = "registerAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;",
            at = @At(
                    value = "INVOKE",
                    // 注意：在高版本中，add 的第一个参数通常是 Holder<Attribute>
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"
            )
    )
    private static AttributeSupplier.Builder doubleHealth(AttributeSupplier.Builder builder, Holder<Attribute> attribute, double value) {
        if (attribute.is(Attributes.MAX_HEALTH)) {
            return builder.add(attribute, value * 2.0);
        }
        return builder.add(attribute, value);
    }
}