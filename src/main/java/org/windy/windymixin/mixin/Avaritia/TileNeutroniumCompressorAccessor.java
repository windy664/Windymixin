package org.windy.windymixin.mixin.Avaritia;

import net.byAqua3.avaritia.tile.TileNeutroniumCompressor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileNeutroniumCompressor.class)
public interface TileNeutroniumCompressorAccessor {
    @Accessor("compressionTarget") int getCompressionTarget();
    @Accessor("compressionTarget") void setCompressionTarget(int value);

    @Accessor("consumptionProgress") int getConsumptionProgress();
    @Accessor("consumptionProgress") void setConsumptionProgress(int value);

    @Accessor("compressionProgress") int getCompressionProgress();
    @Accessor("compressionProgress") void setCompressionProgress(int value);

    @Accessor("targetStack") ItemStack getTargetStack();
}