package org.windy.windymixin.mixin.Avaritia;

import net.byAqua3.avaritia.tile.TileNeutroniumCompressor;
import net.byAqua3.avaritia.recipe.RecipeCompressor;
import net.byAqua3.avaritia.singularity.Singularity;
import net.byAqua3.avaritia.util.AvaritiaRecipeUtils;
import net.byAqua3.avaritia.loader.AvaritiaSingularities;
import net.byAqua3.avaritia.item.ItemJsonSingularity;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 省略 import

@Mixin(TileNeutroniumCompressor.class)
public abstract class TileNeutroniumCompressorMixin {
    @Unique private ItemStack avaritia$cacheTargetStack = ItemStack.EMPTY;
    @Unique private Level avaritia$cacheLevel = null;
    @Unique private int avaritia$cacheSingularityIndex = -2;

    @Shadow @Final @Mutable public ContainerData dataAccess;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void windymixin$injectOptimizedDataAccess(CallbackInfo ci) {
        TileNeutroniumCompressor self = (TileNeutroniumCompressor)(Object)this;
        this.dataAccess = new ContainerData() {
            private void refreshCache() {
                ItemStack currTarget = ((TileNeutroniumCompressorAccessor)self).getTargetStack();
                Level currLevel = self.getLevel(); // 直接用方法
                boolean update = false;
                if (!ItemStack.isSameItemSameComponents(currTarget, avaritia$cacheTargetStack)) {
                    avaritia$cacheTargetStack = currTarget.copy();
                    update = true;
                }
                if (currLevel != avaritia$cacheLevel) {
                    avaritia$cacheLevel = currLevel;
                    update = true;
                }
                if (update) {
                    avaritia$cacheSingularityIndex = -2;
                }
            }
            private int getSingularityIndex() {
                if (avaritia$cacheSingularityIndex == -2) {
                    ItemStack currTarget = ((TileNeutroniumCompressorAccessor)self).getTargetStack();
                    if (currTarget.getItem() instanceof ItemJsonSingularity) {
                        String singularityId = currTarget.getOrDefault(AvaritiaDataComponents.SINGULARITY_ID, "null");
                        Singularity singularity = AvaritiaSingularities.getInstance().getSingularity(singularityId);
                        if (singularity != null) {
                            avaritia$cacheSingularityIndex = AvaritiaSingularities.getInstance().getSingularities().indexOf(singularity);
                        } else {
                            avaritia$cacheSingularityIndex = -1;
                        }
                    } else {
                        avaritia$cacheSingularityIndex = -1;
                    }
                }
                return avaritia$cacheSingularityIndex;
            }
            @Override
            public int get(int index) {
                refreshCache();
                switch (index) {
                    case 0:
                        return ((TileNeutroniumCompressorAccessor)self).getCompressionTarget();
                    case 1:
                        return ((TileNeutroniumCompressorAccessor)self).getConsumptionProgress();
                    case 2:
                        return ((TileNeutroniumCompressorAccessor)self).getCompressionProgress();
                    case 3:
                        return BuiltInRegistries.ITEM.getId(avaritia$cacheTargetStack.getItem());
                    case 4:
                        return getSingularityIndex();
                    default:
                        return 0;
                }
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ((TileNeutroniumCompressorAccessor)self).setCompressionTarget(value);
                    case 1 -> ((TileNeutroniumCompressorAccessor)self).setConsumptionProgress(value);
                    case 2 -> ((TileNeutroniumCompressorAccessor)self).setCompressionProgress(value);
                }
            }
            @Override
            public int getCount() {
                return 5;
            }
        };
    }
}