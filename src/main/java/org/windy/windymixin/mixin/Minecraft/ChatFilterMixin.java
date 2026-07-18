package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 过滤进服时各模组刷屏的无用欢迎/警告信息。
 */
@Mixin(ChatComponent.class)
public class ChatFilterMixin {

    /**
     * 消息内容包含任意关键词即拦截。
     * 覆盖所有非分隔线行:
     *   [JEG] 命令, /justEnoughGuns ..., [Nerospace] ...,
     *   欢迎反馈...nerospace, 感谢您使用Yuushya..., [通用机械] ...
     */
    private static final List<String> SPAM_KEYWORDS = List.of(
            "[JEG]",
            "justEnoughGuns",
            "Nerospace",
            "nerospace",
            "Yuushya",
            "通用机械"
    );

    /**
     * 纯分隔线: 20个以上连续 '-'。
     * 只在紧跟spam块出现时一起拦截，单独出现放行。
     */
    private static final Pattern SEPARATOR = Pattern.compile("^-{20,}$");

    /** 上一条消息是否被判定为spam（用于吞掉紧跟的分隔线） */
    private static boolean lastWasSpam = false;

    @Inject(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void windymixin$filterSpam(
            Component contents,
            @Nullable MessageSignature signature,
            GuiMessageSource source,
            @Nullable GuiMessageTag tag,
            CallbackInfo ci
    ) {
        // 玩家消息放行，只过滤系统消息
        if (source == GuiMessageSource.PLAYER) {
            lastWasSpam = false;
            return;
        }

        String text = contents.getString();

        // 1) 关键词命中 → 标记spam并拦截
        for (String keyword : SPAM_KEYWORDS) {
            if (text.contains(keyword)) {
                lastWasSpam = true;
                ci.cancel();
                return;
            }
        }

        // 2) 纯分隔线，紧跟spam块 → 拦截
        if (lastWasSpam && SEPARATOR.matcher(text.strip()).matches()) {
            ci.cancel();
            return;
        }

        // 3) 其他消息 → 重置标记，放行
        lastWasSpam = false;
    }
}
