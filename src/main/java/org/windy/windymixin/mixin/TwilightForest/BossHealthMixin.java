package org.windy.windymixin.mixin.TwilightForest;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import twilightforest.entity.boss.*;

/**
 * 暮色森林 Boss 血量调整 — 适配枪械高DPS
 */
public class BossHealthMixin {

    // ========== Naga 娜迦: 120 → 500 ==========
    @Mixin(Naga.class)
    public static class NagaHealth {
        @ModifyArg(method = "registerAttributes", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
                index = 1, slice = @Slice(from = @At(value = "FIELD",
                target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;MAX_HEALTH:Lnet/minecraft/core/Holder;")))
        private static double modifyHealth(double original) {
            return 500.0;
        }
    }

    // ========== Lich 巫妖: 100 → 500 ==========
    @Mixin(Lich.class)
    public static class LichHealth {
        @ModifyArg(method = "registerAttributes", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
                index = 1, slice = @Slice(from = @At(value = "FIELD",
                target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;MAX_HEALTH:Lnet/minecraft/core/Holder;")))
        private static double modifyHealth(double original) {
            return 500.0;
        }
    }

    // ========== Hydra 九头蛇: 360 → 2000 ==========
    @Mixin(Hydra.class)
    public static class HydraHealth {
        @ModifyArg(method = "registerAttributes", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
                index = 1, slice = @Slice(from = @At(value = "FIELD",
                target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;MAX_HEALTH:Lnet/minecraft/core/Holder;")))
        private static double modifyHealth(double original) {
            return 2000.0;
        }
    }

    // ========== AlphaYeti 雪怪首领: 200 → 1000 ==========
    @Mixin(AlphaYeti.class)
    public static class AlphaYetiHealth {
        @ModifyArg(method = "registerAttributes", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
                index = 1, slice = @Slice(from = @At(value = "FIELD",
                target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;MAX_HEALTH:Lnet/minecraft/core/Holder;")))
        private static double modifyHealth(double original) {
            return 1000.0;
        }
    }

    // ========== KnightPhantom 幽灵骑士: 35 → 300 ==========
    @Mixin(KnightPhantom.class)
    public static class KnightPhantomHealth {
        @ModifyArg(method = "registerAttributes", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
                index = 1, slice = @Slice(from = @At(value = "FIELD",
                target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;MAX_HEALTH:Lnet/minecraft/core/Holder;")))
        private static double modifyHealth(double original) {
            return 300.0;
        }
    }

    // ========== Minoshroom 牛头人: 120 → 800 ==========
    @Mixin(Minoshroom.class)
    public static class MinoshroomHealth {
        @ModifyArg(method = "registerAttributes", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
                index = 1, slice = @Slice(from = @At(value = "FIELD",
                target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;MAX_HEALTH:Lnet/minecraft/core/Holder;")))
        private static double modifyHealth(double original) {
            return 800.0;
        }
    }

    // ========== SnowQueen 雪女王: 200 → 1200 ==========
    @Mixin(SnowQueen.class)
    public static class SnowQueenHealth {
        @ModifyArg(method = "registerAttributes", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
                index = 1, slice = @Slice(from = @At(value = "FIELD",
                target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;MAX_HEALTH:Lnet/minecraft/core/Holder;")))
        private static double modifyHealth(double original) {
            return 1200.0;
        }
    }

    // ========== UrGhast 恶灵巫妖: 250 → 2000 ==========
    @Mixin(UrGhast.class)
    public static class UrGhastHealth {
        @ModifyArg(method = "registerAttributes", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"),
                index = 1, slice = @Slice(from = @At(value = "FIELD",
                target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;MAX_HEALTH:Lnet/minecraft/core/Holder;")))
        private static double modifyHealth(double original) {
            return 2000.0;
        }
    }
}
