package org.windy.windymixin.mixin.ModularMachineryReborn;

import es.degrassi.mmreborn.common.util.Utils;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Utils.class)
public class UtilsMixin {
    @Unique
    private static int safeNextIntBetweenInclusive(RandomSource random, int min, int max) {
        if (max < min) {
            int t = min;
            min = max;
            max = t;
        }
        long diff = (long) max - (long) min + 1L;
        if (diff <= 0L || diff > Integer.MAX_VALUE) {
            System.err.println("[WindyMixin-Fix] Utils.safeNextIntBetweenInclusive 参数非法: min=" + min + " max=" + max + "，已兜底为min");
            return 1;
        }
        try {
            return random.nextIntBetweenInclusive(min, max);
        } catch (IllegalArgumentException e) {
            System.err.println("[WindyMixin-Fix] Utils.safeNextIntBetweenInclusive 调用异常: " + e + "，已兜底为min");
            return 1;
        }
    }

    @Unique
    private static int safeNextInt(RandomSource random, int bound) {
        if (bound <= 0) {
            System.err.println("[WindyMixin-Fix] Utils.safeNextInt bound非法: " + bound + "，已兜底为0");
            return 1;
        }
        return random.nextInt(bound);
    }
}