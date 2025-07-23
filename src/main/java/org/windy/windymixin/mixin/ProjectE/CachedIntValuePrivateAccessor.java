package org.windy.windymixin.mixin.ProjectE;

import moze_intel.projecte.config.value.CachedIntValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CachedIntValue.class)
public interface CachedIntValuePrivateAccessor {
    @Accessor("resolved")
    boolean getResolved();
    @Accessor("resolved")
    void setResolved(boolean value);
    @Accessor("cachedValue")
    int getCachedValue();
    @Accessor("cachedValue")
    void setCachedValue(int value);
}