package org.windy.windymixin.mixin.ProjectE;

import moze_intel.projecte.config.value.CachedDoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;


import java.lang.reflect.Field;

@Mixin(CachedDoubleValue.class)
public abstract class CachedDoubleValueMixin {


    @Overwrite
    public double get() {
        try {

            Field resolvedField = this.getClass().getSuperclass().getDeclaredField("resolved");
            Field cachedValueField = this.getClass().getSuperclass().getDeclaredField("cachedValue");
            Field internalField = this.getClass().getSuperclass().getDeclaredField("internal");
            resolvedField.setAccessible(true);
            cachedValueField.setAccessible(true);
            internalField.setAccessible(true);

            boolean resolved = resolvedField.getBoolean(this);
            if (!resolved) {
                ModConfigSpec.ConfigValue<Double> internal = (ModConfigSpec.ConfigValue<Double>) internalField.get(this);
                try {
                    double value = internal.get();
                    cachedValueField.setDouble(this, value);
                    resolvedField.setBoolean(this, true);
                } catch (IllegalStateException e) {
                    // 兜底
                    return internal.getDefault();
                }
            }
            return cachedValueField.getDouble(this);
        } catch (Exception e) {
            // 反射异常兜底
            return 0.0;
        }
    }
}