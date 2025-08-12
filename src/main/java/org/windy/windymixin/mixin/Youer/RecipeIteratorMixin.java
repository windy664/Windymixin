package org.windy.windymixin.mixin.Youer;

import org.bukkit.craftbukkit.inventory.RecipeIterator;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;

@Mixin(RecipeIterator.class)
public class RecipeIteratorMixin {
    @Shadow
    private Iterator<Map.Entry<?, ?>> recipes;
    @Shadow
    private Recipe currentRecipe;

    @Inject(method = "next", at = @At("HEAD"), cancellable = true)
    private void skipNullOrBrokenRecipe(CallbackInfoReturnable<Recipe> cir) {
        Recipe recipe = null;
        while (this.recipes.hasNext() && recipe == null) {
            try {
                Object entry = this.recipes.next().getValue();
                // 反射调用 toBukkitRecipe
                recipe = (Recipe) entry.getClass().getMethod("toBukkitRecipe").invoke(entry);
            } catch (Throwable t) {
                recipe = null; // skip this recipe
            }
        }
        this.currentRecipe = recipe;
        if (recipe == null) {
            throw new java.util.NoSuchElementException();
        }
        cir.setReturnValue(recipe);
    }
}