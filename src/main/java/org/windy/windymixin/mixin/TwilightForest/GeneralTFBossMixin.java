package org.windy.windymixin.mixin.TwilightForest;


import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class GeneralTFBossMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onBossInit(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getClass().getName().startsWith("twilightforest.entity.boss")) {
            var maxHealth = entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                double doubleHealth = maxHealth.getBaseValue() * 5.0;
                maxHealth.setBaseValue(doubleHealth);
                entity.setHealth((float) doubleHealth);
            }
        }
    }
}