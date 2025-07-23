package org.windy.windymixin.mixin.Neoforge;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(NetworkRegistry.class)
public interface NetworkRegistryAccessor {
    @Accessor("BUILTIN_PAYLOADS")
    static Map<ResourceLocation, ?> getBuiltinPayloads() {
        throw new AssertionError();
    }
}