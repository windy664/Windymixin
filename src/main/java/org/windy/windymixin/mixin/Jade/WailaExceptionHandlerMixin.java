package org.windy.windymixin.mixin.Jade;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import snownee.jade.util.WailaExceptionHandler;

import java.util.function.Consumer;

@Mixin(WailaExceptionHandler.class)
public class WailaExceptionHandlerMixin {

    @Redirect(
            method = "handleErr",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"
            )
    )
    private static void onlyBlockJadeErrorTooltip(Consumer tooltip, Object component) {
        // 支持1.18及以下
        try {
            Class<?> translatable = Class.forName("net.minecraft.network.chat.TranslatableComponent");
            if (translatable.isInstance(component)) {
                String key = (String) translatable.getMethod("getKey").invoke(component);
                if ("jade.error".equals(key)) {
                    return;
                }
            }
        } catch (Exception ignored) {
        }
        // 支持1.19及以后
        try {
            Class<?> mutableComponent = Class.forName("net.minecraft.network.chat.MutableComponent");
            Class<?> translatableContents = Class.forName("net.minecraft.network.chat.contents.TranslatableContents");
            if (mutableComponent.isInstance(component)) {
                Object contents = mutableComponent.getMethod("getContents").invoke(component);
                if (translatableContents.isInstance(contents)) {
                    String key = (String) translatableContents.getMethod("getKey").invoke(contents);
                    if ("jade.error".equals(key)) {
                        return;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        tooltip.accept(component);
    }
}