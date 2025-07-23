package org.windy.windymixin.mixin.IndustrialUpgrade;

import com.denfop.api.energy.EnergyNetLocal;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnergyNetLocal.class)
public class EnergyNetLocalMixin {
    @Inject(
            method = "explodeMachineAt",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void crashGuard(BlockEntity entity, CallbackInfo ci) {
        if (entity == null) {
            System.err.println("[风吟的通用BUG修复] 工业升级的explodeMachineAt 被传入 null，已拦截，防止崩服。");
            ci.cancel();
        }
    }
}

