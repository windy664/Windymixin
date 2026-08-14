package org.windy.windymixin.mixin.Mekanism;

import mekanism.common.block.BlockMekanism;
import mekanism.common.lib.security.ISecurityTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复客户端侧设置安全所有者导致的状态不一致。
 *
 * 原版 setPlacedBy 中 setOwnerUUID 不分客户端/服务端都会执行，
 * 导致客户端侧创建安全频率，引发客户端-服务端状态不同步。
 *
 * @author Windy
 */
@Mixin(value = BlockMekanism.class, remap = false)
public class BlockMekanismSecurityMixin {

    /**
     * 在 setOwnerUUID 调用前注入客户端检查。
     * 如果是客户端侧，取消 setOwnerUUID 调用。
     * 使用 require=0 兼容已修复的版本。
     */
    @Inject(
        method = "setPlacedBy",
        at = @At(
            value = "INVOKE",
            target = "Lmekanism/common/lib/security/ISecurityTile;setOwnerUUID(Ljava/util/UUID;Lnet/neoforged/neoforge/transfer/transaction/TransactionContext;)V"
        ),
        cancellable = true,
        require = 0
    )
    private void windymixin$skipOwnerOnClient(Level world, BlockPos pos, BlockState state,
                                               @Nullable LivingEntity placer, ItemStack stack,
                                               CallbackInfo ci) {
        if (world.isClientSide()) {
            // 客户端侧跳过 setOwnerUUID，由服务端处理并通过 PacketSyncSecurity 同步
            ci.cancel();
        }
    }
}
