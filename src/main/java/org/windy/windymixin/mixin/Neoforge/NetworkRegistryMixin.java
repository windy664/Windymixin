package org.windy.windymixin.mixin.Neoforge;


import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkRegistry.class)
public class NetworkRegistryMixin {
    @Inject(
            method = "hasChannel(Lnet/minecraft/network/Connection;Lnet/minecraft/network/ConnectionProtocol;Lnet/minecraft/resources/ResourceLocation;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void allowProjectEKeyPress(
            Connection connection,
            ConnectionProtocol protocol,
            ResourceLocation id,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (protocol == ConnectionProtocol.PLAY
                &&( "projecte".equals(id.getNamespace()) || "modern_industrialization".equals(id.getNamespace()))){
            cir.setReturnValue(true);
        }
    }
}