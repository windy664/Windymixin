package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.windy.windymixin.Config;
import org.windy.windymixin.Windymixin;

/**
 * 魔改末影龙最大血量（配置驱动）。
 *
 * <p>在 {@code EnderDragon.<init>} 末尾（原版此时已 setHealth(getMaxHealth())）重写
 * MAX_HEALTH 属性基础值，并把当前血量拉满到新上限。倍率/绝对值均由
 * {@code config/windymixin.json} 的 {@code ender_dragon} 段控制。</p>
 *
 * <p>26.2 构造签名：{@code EnderDragon(EntityType, Level)}；属性名 {@code Attributes.MAX_HEALTH}。</p>
 */
@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void windymixin$scaleMaxHealth(EntityType<? extends EnderDragon> type, Level level, CallbackInfo ci) {
        Config.EnderDragon cfg = Config.CONFIG == null ? null : Config.CONFIG.ender_dragon;
        if (cfg == null || !cfg.enabled) {
            return;
        }
        if (cfg.max_health <= 0.0) {
            return;
        }

        LivingEntity self = (LivingEntity) (Object) this;
        AttributeInstance attr = self.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) {
            return;
        }
        attr.setBaseValue(cfg.max_health);
        self.setHealth((float) cfg.max_health);
        Windymixin.LOGGER.info("[Windymixin] 末影龙最大血量已设为 {}。", cfg.max_health);
    }
}
