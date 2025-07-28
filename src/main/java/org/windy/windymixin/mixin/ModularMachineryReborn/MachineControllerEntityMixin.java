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
     * 防止传入非法参数导致崩溃，兜底返回 0 或 min。
     */
    @Redirect(
            method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextIntBetweenInclusive(II)I"
            )
    )
    private int safeNextIntBetweenInclusive(RandomSource instance, int min, int max) {
        if (max < min) {
            int t = min;
            min = max;
            max = t;
        }
        long diff = (long)max - (long)min + 1L;
        if (diff <= 0L || diff > Integer.MAX_VALUE) {
            String msg = "[Mixin-Fix] MachineControllerEntity 构造时 tickOffset 随机数参数异常（区间非法），已兜底返回 min，严重建议修复MOD代码！min=" + min + " max=" + max;
            System.err.println(msg);
            return 1;
        }
        try {
            return instance.nextIntBetweenInclusive(min, max);
        } catch (IllegalArgumentException e) {
            String msg = "[Mixin-Fix] MachineControllerEntity 构造时 tickOffset 随机数调用异常: " + e + "，已兜底返回 min，严重建议修复MOD代码！";
            System.err.println(msg);
            return 1;
        }
    }
}