package org.windy.windymixin.mixin.Minecraft;


import ca.spottedleaf.moonrise.common.util.SimpleRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import ca.spottedleaf.moonrise.common.util.SimpleRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleRandom.class)
public class SimpleRandomMixin {
    @Shadow
    private long advanceSeed() {
        throw new AssertionError(); // Mixin 会忽略实现体
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int nextInt(int bound) {
        if (bound <= 0) {
            System.err.println("[Mixin-Fix] SimpleRandom.nextInt(" + bound + ") 参数非法，已兜底返回0。");
            return 0;
        }
        long value = this.advanceSeed() >>> 16;
        return (int)(value * (long)bound >>> 32);
    }
}