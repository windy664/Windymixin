package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 末影龙血量魔改：200 → 1200（有暮色森林时末影龙是中等boss）。
 *
 * <p>用 @ModifyArg 改 createAttributes() 里的 MAX_HEALTH 基础值，
 * 新生成和存档加载都生效。</p>
 */
@Mixin(EnderDragon.class)
public class EnderDragonMixin {

    @ModifyArg(method = "createAttributes", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
            index = 1)
    private static double modifyHealth(double original) {
        return 1200.0;
    }
}
