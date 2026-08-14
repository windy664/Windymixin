package org.windy.windymixin.mixin.Mekanism;

import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import net.minecraft.core.TypedInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * 修复 ItemStackIngredient.testType 跳过组件数据检查的问题。
 *
 * 原版对非自定义 Ingredient 使用 acceptsItem() 跳过组件数据检查，
 * 但某些模组（如神秘农业）的配方依赖组件数据（如作物标签），
 * acceptsItem() 会导致这些配方匹配错误。
 *
 * 统一使用 test() 方法确保组件数据被正确检查。
 *
 * @author Windy
 * @reason acceptsItem 跳过组件数据导致配方匹配错误
 */
@Mixin(value = ItemStackIngredient.class, remap = false)
public class ItemStackIngredientFixMixin {

    /**
     * @author Windy
     * @reason 始终使用 test() 检查组件数据，避免 acceptsItem 跳过检查
     */
    @Overwrite
    public boolean testType(TypedInstance<Item> instance) {
        java.util.Objects.requireNonNull(instance);
        Ingredient vanillaIngredient = ((ItemStackIngredient) (Object) this).ingredient().ingredient();
        // 组件数据可能影响匹配结果，始终创建 ItemStack 进行完整测试
        return vanillaIngredient.test(IngredientCreatorAccess.item().createStack(instance));
    }
}
