package org.windy.windymixin.mixin.ProjectE;

import moze_intel.projecte.config.value.CachedIntValue;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CachedIntValue.class)
public abstract class CachedIntValueMixin {

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

            //兜底
            return internal.getDefault();
        }
    }
}