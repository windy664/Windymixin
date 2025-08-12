package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(StringUtil.class)
public class StringUtilMixin {

    @Overwrite
    public static boolean isAllowedChatCharacter(char character) {
        return character >= ' ' && character != 127;
    }
}
