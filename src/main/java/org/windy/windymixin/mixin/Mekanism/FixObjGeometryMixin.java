package org.windy.windymixin.mixin.Mekanism;

import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.neoforged.neoforge.client.model.obj.ObjGeometry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "mekanism.client.model.BaseModelCache$OBJModelData$ObjModelSettings", remap = false)
public class FixObjGeometryMixin {

    @Shadow
    private ResolvedModel resolvedModel;

    /**
     * @author Windy
     * @reason Fix ClassCastException when Create mod's ObjGeometry is loaded
     */
    @Overwrite
    private ObjGeometry geometry() {
        UnbakedGeometry geometry = resolvedModel.getTopGeometry();
        if (geometry instanceof ObjGeometry objGeometry) {
            return objGeometry;
        }
        // If it's not a NeoForge ObjGeometry (e.g., Create's ObjGeometry),
        // return null to avoid ClassCastException
        return null;
    }
}
