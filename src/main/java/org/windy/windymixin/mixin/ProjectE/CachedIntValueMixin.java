package org.windy.windymixin.mixin.ProjectE;

import moze_intel.projecte.config.value.CachedIntValue;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CachedIntValue.class)
public abstract class CachedIntValueMixin {
    /**
     * 临时修复 ProjectE 崩溃：config 未加载时返回默认值
     */
    @Overwrite
    public int get() {
        CachedIntValuePrivateAccessor self = (CachedIntValuePrivateAccessor) (Object) this;
        ModConfigSpec.ConfigValue<Integer> internal =
                ((CachedValueAccessor) this).getInternal();
        try {
            if (!self.getResolved()) {
                self.setCachedValue(internal.get());
                self.setResolved(true);
            }
            return self.getCachedValue();
        } catch (IllegalStateException e) {
            // config 尚未加载，返回默认值
            return internal.getDefault();
        }
    }
}