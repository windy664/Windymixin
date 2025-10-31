package org.windy.windymixin.mixin.TwilightForest;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.block.TrollsteinnBlock;

@Mixin(TrollsteinnBlock.class)
public class TrollsteinnBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void preventServerParticle(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!level.isClientSide()) {
            ci.cancel();
        }
    }
}