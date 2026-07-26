package org.windy.windymixin.mixin.JEG;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ttv.migami.jeg.gun.BallisticProtection;
import ttv.migami.jeg.gun.GunStats;

/**
 * 让 JEG 的弹道系统识别 MekaSuit 护甲。
 *
 * JEG 原版 BallisticProtection.applyToArmorHit 只认 BulletproofArmorItem，
 * MekaSuit 走到 instanceOf 判断会被完全无视（原样返回 rawDamage）。
 * 本 mixin 在方法 HEAD 检测到 MekaSuit 时，用自定义弹道评级拦截返回。
 */
@Mixin(value = BallisticProtection.class, remap = false)
public abstract class BallisticMixin {

    // ===== 可调参数（config 驱动优先，这里给默认值）=====

    /** MekaSuit 弹道评级 — 对标 JEG Tier V (5.20) ~ VI (6.20)，MekaSuit 是终局装备 */
    @Unique private static final float MEKASUIT_BALLISTIC_RATING = 5.60F;

    /** 低穿甲（弹头穿甲 < 护甲评级）时的伤害倍率，越低越硬 */
    @Unique private static final float MEKASUIT_UNDERMATCH_MULTIPLIER = 0.15F;

    /** 高穿甲（弹头穿甲 >= 护甲评级）时的伤害倍率 */
    @Unique private static final float MEKASUIT_OVERMATCH_MULTIPLIER = 0.80F;

    /** 耐久消耗系数 — MekaSuit 走能量不走耐久，但 JEG 会调 hurtAndBreak，给低值 */
    @Unique private static final float MEKASUIT_DURABILITY_SCALE = 0.10F;

    /**
     * 注入到 5 参数版 applyToArmorHit 的 HEAD。
     * 检测到 MekaSuit 类名时拦截，走自定义弹道逻辑。
     */
    @Inject(
        method = "applyToArmorHit(FLttv/migami/jeg/gun/GunStats;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Z)Lttv/migami/jeg/gun/BallisticProtection$BallisticResult;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void windymixin$onApplyToArmorHit5(
            float rawDamage,
            GunStats stats,
            ItemStack armorStack,
            EquipmentSlot slot,
            boolean rocketDirectHit,
            CallbackInfoReturnable<BallisticProtection.BallisticResult> cir
    ) {
        if (isMekaSuitArmor(armorStack)) {
            cir.setReturnValue(windymixin$computeMekaSuitResult(rawDamage, stats, rocketDirectHit, slot));
        }
    }

    /**
     * 注入到 6 参数版 applyToArmorHit 的 HEAD（带 armorPiercingMultiplier）。
     */
    @Inject(
        method = "applyToArmorHit(FLttv/migami/jeg/gun/GunStats;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ZF)Lttv/migami/jeg/gun/BallisticProtection$BallisticResult;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void windymixin$onApplyToArmorHit6(
            float rawDamage,
            GunStats stats,
            ItemStack armorStack,
            EquipmentSlot slot,
            boolean rocketDirectHit,
            float armorPiercingMultiplier,
            CallbackInfoReturnable<BallisticProtection.BallisticResult> cir
    ) {
        if (isMekaSuitArmor(armorStack)) {
            cir.setReturnValue(windymixin$computeMekaSuitResult(rawDamage, stats, rocketDirectHit, slot, armorPiercingMultiplier));
        }
    }

    // ===== 内部逻辑 =====

    @Unique
    private static boolean isMekaSuitArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // 软检测：类名包含 MekaSuit，避免硬依赖 Mekanism
        String className = stack.getItem().getClass().getName();
        return className.contains("MekaSuit") || className.contains("ItemMekaSuitArmor");
    }

    @Unique
    private static BallisticProtection.BallisticResult windymixin$computeMekaSuitResult(
            float rawDamage, GunStats stats, boolean rocketDirectHit, EquipmentSlot slot
    ) {
        return windymixin$computeMekaSuitResult(rawDamage, stats, rocketDirectHit, slot, 1.0F);
    }

    @Unique
    private static BallisticProtection.BallisticResult windymixin$computeMekaSuitResult(
            float rawDamage, GunStats stats, boolean rocketDirectHit, EquipmentSlot slot, float apMultiplier
    ) {
        if (rawDamage <= 0) {
            return new BallisticProtection.BallisticResult(rawDamage, 0, false, false);
        }

        // 复用 JEG 的穿甲计算
        float effectiveAP = BallisticProtection.effectiveArmorPiercing(stats, rocketDirectHit, apMultiplier);

        // 头盔评级打 8 折（和 JEG BulletproofArmorItem 一致）
        float rating = (slot == EquipmentSlot.HEAD)
                ? MEKASUIT_BALLISTIC_RATING * 0.80F
                : MEKASUIT_BALLISTIC_RATING;

        if (rating <= 0) {
            return new BallisticProtection.BallisticResult(rawDamage, 0, false, false);
        }

        float apRatio = effectiveAP / rating;
        boolean overmatched = effectiveAP >= rating;

        // 伤害倍率计算（和 JEG apply() 逻辑一致）
        float damageMultiplier;
        if (overmatched) {
            damageMultiplier = Mth.clamp(apRatio, 1.00F, 1.50F) * MEKASUIT_OVERMATCH_MULTIPLIER;
            damageMultiplier = Math.min(damageMultiplier, 0.95F);
        } else {
            damageMultiplier = Mth.clamp(apRatio, 0.05F, 0.95F) * MEKASUIT_UNDERMATCH_MULTIPLIER;
        }

        // 耐久消耗（MekaSuit 走能量，给低值防止意外碎甲）
        float pressure = effectiveAP / rating;
        float durabilityMultiplier;
        if (!overmatched) {
            durabilityMultiplier = 0.35F + pressure * 0.65F;
        } else {
            durabilityMultiplier = 1.00F + Math.min(pressure - 1.00F, 1.00F) * 1.25F;
        }
        float slotScale = (slot == EquipmentSlot.HEAD) ? 1.15F : 1.00F;
        int durabilityDamage = Mth.clamp(
                Mth.ceil(rawDamage * durabilityMultiplier * slotScale * MEKASUIT_DURABILITY_SCALE),
                0,  // MekaSuit 不消耗耐久，给 0
                40
        );

        float finalDamage = rawDamage * damageMultiplier;
        return new BallisticProtection.BallisticResult(finalDamage, durabilityDamage, true, overmatched);
    }
}
