package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Inject(method = "isChatHidden", at = @At("HEAD"), cancellable = true)
    private void forceShowChat(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
