package org.windy.windymixin.mixin.SophisticatedBackpacks;

import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BasicBackpackRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 修复：精妙背包(Sophisticated Backpacks) 在 Youer 26.2 上点击合成台崩服。
 *
 * <p>Youer/CraftBukkit 在 {@code net.minecraft.world.item.crafting.Recipe} 接口上
 * 注入了抽象方法 {@code toBukkitRecipe(NamespacedKey)}。BasicBackpackRecipe 是模组早于
 * 该补丁编译的自定义配方，从未实现该方法，于是运行时（RecipeHolder.toBukkitRecipe ->
 * CraftInventoryCrafting.getRecipe -> handleContainerClick）抛出 AbstractMethodError。</p>
 *
 * <p>这里为其补上该方法并返回 {@code null} —— 表示"没有对应的 Bukkit 配方"，
 * 服务端会安全地跳过 Bukkit 配方转换，不再崩溃。</p>
 */
@Mixin(value = BasicBackpackRecipe.class, remap = false)
public class BasicBackpackRecipeMixin {

    public Recipe toBukkitRecipe(NamespacedKey key) {
        return null;
    }
}
