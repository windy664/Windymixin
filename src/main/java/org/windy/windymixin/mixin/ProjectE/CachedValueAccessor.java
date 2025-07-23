package org.windy.windymixin.mixin.ProjectE;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// 适配父类 CachedValue<T> （用 targets 字符串防止泛型问题）
@Mixin(targets = "moze_intel.projecte.config.value.CachedValue")
public interface CachedValueAccessor {
    @Accessor("internal")
    ModConfigSpec.ConfigValue<Integer> getInternal();
}