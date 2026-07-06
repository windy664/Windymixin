package org.windy.windymixin.mixin.Minecraft;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 暴露 {@link BlockEntity#level} 私有字段，供其它逻辑读取方块实体所在世界。
 * 26.2 字段仍为 {@code protected Level level;}。
 */
@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {
    @Accessor("level")
    Level getLevelOrWorld();
}
