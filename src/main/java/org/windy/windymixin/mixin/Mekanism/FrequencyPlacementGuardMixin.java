package org.windy.windymixin.mixin.Mekanism;

import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

/**
 * 修复 TileComponentSecurity.setOwnerUUID 在客户端侧设置频率的问题。
 */
@Mixin(value = TileComponentSecurity.class, remap = false)
public class FrequencyPlacementGuardMixin {

    @Shadow
    private TileEntityMekanism tile;

    /**
     * 使用 @Redirect 重定向 setFrequency 调用，在客户端侧跳过频率设置
     */
    @Redirect(
            method = "setOwnerUUID",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/tile/base/TileEntityMekanism;setFrequency(Lmekanism/common/lib/frequency/FrequencyType;Lmekanism/common/lib/frequency/Frequency$FrequencyIdentity;Ljava/util/UUID;)V"
            ),
            require = 0
    )
    private void windymixin$redirectSetFrequency(TileEntityMekanism instance, FrequencyType<?> type, Frequency.FrequencyIdentity identity, UUID uuid) {
        Level level = instance.getLevel();
        // 只在服务端设置频率
        if (level == null || !level.isClientSide()) {
            instance.setFrequency(type, identity, uuid);
        }
    }
}