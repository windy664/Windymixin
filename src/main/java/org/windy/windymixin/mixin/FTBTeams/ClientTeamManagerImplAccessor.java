package org.windy.windymixin.mixin.FTBTeams;

import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientTeamManagerImpl.class)
public interface ClientTeamManagerImplAccessor {
    @Accessor("knownPlayers")
    Map<UUID, KnownClientPlayer> getKnownPlayers();

    @Accessor("selfKnownPlayer")
    void setSelfKnownPlayer(KnownClientPlayer kcp);
}