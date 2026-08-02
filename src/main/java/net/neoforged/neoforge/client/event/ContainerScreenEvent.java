/*
 * 兼容性垫片：NeoForge 26.2.0.40-beta 移除了 ContainerScreenEvent.Render.Foreground，
 * 将其合并到 ScreenEvent.Render.Foreground。旧版 mod（Sophisticated Core、Architectury 等）
 * 仍引用已删除的类，导致 NoClassDefFoundError。
 *
 * 此垫片类仅供类加载使用，实际事件订阅由 EventBusMixin 重定向到 ScreenEvent.Render.Foreground。
 */

package net.neoforged.neoforge.client.event;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

/**
 * 兼容性垫片 — 旧版 NeoForge 的 ContainerScreenEvent。
 * 新版已将此类合并到 {@link ScreenEvent.Render.Foreground}。
 *
 * @see ScreenEvent.Render.Foreground
 */
public abstract class ContainerScreenEvent extends Event {
    private final AbstractContainerScreen<?> containerScreen;

    @ApiStatus.Internal
    protected ContainerScreenEvent(AbstractContainerScreen<?> containerScreen) {
        this.containerScreen = containerScreen;
    }

    public AbstractContainerScreen<?> getContainerScreen() {
        return containerScreen;
    }

    /**
     * 兼容性垫片 — 旧版 Render 内部类。
     */
    public static abstract class Render extends ContainerScreenEvent {
        private final GuiGraphicsExtractor guiGraphics;
        private final int mouseX;
        private final int mouseY;

        @ApiStatus.Internal
        protected Render(AbstractContainerScreen<?> guiContainer, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
            super(guiContainer);
            this.guiGraphics = guiGraphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public GuiGraphicsExtractor getGuiGraphics() {
            return guiGraphics;
        }

        public int getMouseX() {
            return mouseX;
        }

        public int getMouseY() {
            return mouseY;
        }

        /**
         * 兼容性垫片 — 旧版 Foreground 事件。
         * 实际事件订阅通过 EventBusMixin 重定向到 {@link ScreenEvent.Render.Foreground}。
         */
        public static class Foreground extends Render {
            @ApiStatus.Internal
            public Foreground(AbstractContainerScreen<?> guiContainer, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
                super(guiContainer, guiGraphics, mouseX, mouseY);
            }
        }
    }
}
