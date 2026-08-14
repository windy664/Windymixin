package org.windy.windymixin.mixin.Mekanism;

import mekanism.api.security.SecurityMode;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyController;
import mekanism.common.lib.frequency.FrequencyLookup;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.frequency.FrequencyTypes;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

/**
 * 修复 FrequencyType 中 getController() 返回 null 时的 NPE。
 *
 * getController() 在某些时序下（如方块刚放置、区块刚加载）可能返回 null，
 * 原版直接链式调用会导致 NullPointerException。
 *
 * @author Windy
 * @reason 添加 null 检查，防止 getController() 返回 null 时崩溃
 */
@Mixin(value = FrequencyType.class, remap = false)
public abstract class FrequencyTypeNullGuardMixin<FREQ extends Frequency> {

    @Shadow
    @Nullable
    public abstract FrequencyController<FREQ> getController();

    /**
     * @author Windy
     * @reason 添加 getController() null 检查
     */
    @Overwrite
    @Nullable
    public FrequencyLookup<FREQ> getLookup(@Nullable UUID owner, SecurityMode securityMode) {
        FrequencyController<FREQ> controller = getController();
        if (controller == null) {
            return null;
        }
        return switch (securityMode) {
            case PUBLIC -> controller.getPublicLookup();
            case PRIVATE -> controller.getPrivateLookup(owner);
            case TRUSTED -> controller.getTrustedLookup(owner);
        };
    }

    /**
     * @author Windy
     * @reason 添加 getController() null 检查
     */
    @Overwrite
    @Nullable
    public FrequencyLookup<FREQ> getFrequencyLookup(@Nullable FREQ freq) {
        if (freq == null) {
            return null;
        }
        FrequencyController<FREQ> controller = getController();
        if (controller == null) {
            return null;
        }
        if (freq.getType() == FrequencyTypes.SECURITY) {
            return controller.getPublicLookup();
        }
        return switch (freq.getSecurity()) {
            case PUBLIC -> controller.getPublicLookup();
            case PRIVATE -> controller.getPrivateLookup(freq.getOwner());
            case TRUSTED -> controller.getTrustedLookup(freq.getOwner());
        };
    }

    /**
     * @author Windy
     * @reason 添加 getController() null 检查
     */
    @Overwrite
    @Nullable
    public FrequencyLookup<FREQ> getLookup(Frequency.FrequencyIdentity identity, @Nullable UUID owner) {
        FrequencyController<FREQ> controller = getController();
        if (controller == null) {
            return null;
        }
        return switch (identity.securityMode()) {
            case PUBLIC -> controller.getPublicLookup();
            case PRIVATE -> controller.getPrivateLookup(owner);
            case TRUSTED -> controller.getTrustedLookup(owner);
        };
    }
}
