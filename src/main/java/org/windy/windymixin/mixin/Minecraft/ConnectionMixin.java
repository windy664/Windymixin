package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;


import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Overwrite
    public void disconnect(Component message) {

    }

    @Overwrite
    public void disconnect(DisconnectionDetails disconnectionDetails) {

    }
}