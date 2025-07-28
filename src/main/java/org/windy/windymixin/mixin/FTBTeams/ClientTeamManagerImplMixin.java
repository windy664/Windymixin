package org.windy.windymixin.mixin.FTBTeams;

import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.UUID;


@Mixin(ClientTeamManagerImpl.class)
public class ClientTeamManagerImplMixin {

    @Inject(method = "initSelfDetails", at = @At("TAIL"))
    private void injectOfflineSupport(UUID selfTeamID, CallbackInfo ci) {
        ClientTeamManagerImpl self = (ClientTeamManagerImpl)(Object)this;
        ClientTeamManagerImplAccessor accessor = (ClientTeamManagerImplAccessor) self;
        if (self.self() == null) {
            UUID userId = Minecraft.getInstance().getUser().getProfileId();
            String name = Minecraft.getInstance().getUser().getName();
            KnownClientPlayer fakePlayer = new KnownClientPlayer(userId, name, true, selfTeamID, null, null);
            accessor.getKnownPlayers().put(userId, fakePlayer);
            accessor.setSelfKnownPlayer(fakePlayer);
        }
    }
}