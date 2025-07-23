package org.windy.windymixin.mixin.Neoforge;

import net.neoforged.neoforge.network.negotiation.NetworkComponentNegotiator;
import net.neoforged.neoforge.network.negotiation.NegotiationResult;
import net.neoforged.neoforge.network.negotiation.NegotiatedNetworkComponent;
import net.neoforged.neoforge.network.negotiation.NegotiableNetworkComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Mixin(NetworkComponentNegotiator.class)
public class NetworkComponentNegotiatorMixin {

    @Inject(
            method = "negotiate",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void alwaysSuccess(
            List<NegotiableNetworkComponent> server,
            List<NegotiableNetworkComponent> client,
            CallbackInfoReturnable<NegotiationResult> cir
    ) {

        List<NegotiatedNetworkComponent> agreed = new ArrayList<>();

        for (NegotiableNetworkComponent s : server) {
            for (NegotiableNetworkComponent c : client) {
                if (s.id().equals(c.id())) {
                    agreed.add(new NegotiatedNetworkComponent(s.id(), s.version()));
                }
            }
        }

        cir.setReturnValue(new NegotiationResult(agreed, true, Collections.emptyMap()));
    }
}