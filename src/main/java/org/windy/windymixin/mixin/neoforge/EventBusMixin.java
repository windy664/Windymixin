package org.windy.windymixin.mixin.neoforge;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 兼容性修复：NeoForge 26.2.0.40-beta 删除了 ContainerScreenEvent.Render.Foreground，
 * 将其合并到 ScreenEvent.Render.Foreground。此 mixin 在新版事件 post 之后，
 * 额外 post 旧版事件，让订阅旧事件的 mod 仍能收到回调。
 */
@Mixin(value = AbstractContainerScreen.class, remap = false)
public class EventBusMixin {

    /**
     * 在 extractRenderState 中 post ScreenEvent.Render.Foreground 之后，
     * 追加 post ContainerScreenEvent.Render.Foreground。
     */
    @Inject(
        method = "extractRenderState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/common/NeoForge;EVENT_BUS:Lnet/neoforged/bus/api/IEventBus;",
            shift = At.Shift.AFTER
        )
    )
    private void windymixin$postLegacyForegroundEvent(
            GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick,
            CallbackInfo ci) {
        AbstractContainerScreen<?> self = (AbstractContainerScreen<?>) (Object) this;
        NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Foreground(self, guiGraphics, mouseX, mouseY));
    }
}
