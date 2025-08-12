package org.windy.windymixin.mixin.ModularMachineryReborn;

import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MachineControllerEntity.class)
public class MachineControllerEntityMixin {

    /**
     * 拦截 MachineControllerEntity 构造器里对 RandomSource#nextIntBetweenInclusive 的调用，
     * 修正非法参数，保证 tickOffset 算法安全。
     */
    @Redirect(
            method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextIntBetweenInclusive(II)I"
            )
    )
    private int fixedNextIntBetweenInclusive(RandomSource instance, int min, int max) {
        // 修正算法：保证 min <= max，且差值不溢出
        if (max < min) {
            int t = min;
            min = max;
            max = t;
        }
        long diff = (long)max - (long)min + 1L;
        if (diff <= 0L || diff > Integer.MAX_VALUE) {
            String msg = "[WindyMixin-Fix] [ModularMachineryReborn] MachineControllerEntity 构造随机区间参数非法！min=" + min + ", max=" + max + "，已兜底为min。请MOD作者修复！";
            System.err.println(msg);
            return min;
        }
        try {
            return instance.nextIntBetweenInclusive(min, max);
        } catch (IllegalArgumentException e) {
            String msg = "[WindyMixin-Fix] [ModularMachineryReborn] MachineControllerEntity nextIntBetweenInclusive 调用异常: " + e + "，已兜底为min。请MOD作者修复！";
            System.err.println(msg);
            return min;
        }
    }
}