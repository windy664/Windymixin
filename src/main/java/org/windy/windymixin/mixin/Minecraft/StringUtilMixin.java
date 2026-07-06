package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * 放宽聊天可用字符：原版 {@code isAllowedChatCharacter} 会拦掉 §(167) 等字符。
 * 这里仅拦掉控制字符(< 空格)与 DEL(127)，放行其余（含彩色符号 §）。
 *
 * <p>26.2 签名由 {@code char} 改为 {@code int ch}，此处同步。</p>
 */
@Mixin(StringUtil.class)
public class StringUtilMixin {

    /**
     * @author windymixin
     * @reason 放行 § 等字符用于聊天/告示牌染色
     */
    @Overwrite
    public static boolean isAllowedChatCharacter(int ch) {
        return ch >= ' ' && ch != 127;
    }
}
