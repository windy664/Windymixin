package org.windy.windymixin;

import com.google.gson.JsonElement;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.minecraft.resources.Identifier;

import java.util.Iterator;
import java.util.Map;

/**
 * 通过 NeoForge 的 ModifyRecipeJsonsEvent 拦截 JEG 配方注册。
 *
 * 在 RecipeManager 解析配方 JSON 之前，把 jeg 命名空间的配方全部移除，
 * 由 Windymixin 数据包（data/windymixin/recipe/jeg/）接管配方。
 *
 * 比旧 JegRecipePurge（直接改写 jar）干净得多——不碰第三方文件，纯事件拦截。
 */
@EventBusSubscriber(modid = Windymixin.MODID)
public class JegRecipeFilter {

    private static final String JEG_NAMESPACE = "jeg";

    @SubscribeEvent
    static void onModifyRecipes(ModifyRecipeJsonsEvent event) {
        Map<Identifier, JsonElement> recipes = event.getRecipeJsons();
        int removed = 0;

        Iterator<Map.Entry<Identifier, JsonElement>> it = recipes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Identifier, JsonElement> entry = it.next();
            if (JEG_NAMESPACE.equals(entry.getKey().getNamespace())) {
                it.remove();
                removed++;
            }
        }

        if (removed > 0) {
            Windymixin.LOGGER.info("[Windymixin] ✅ 已通过事件拦截移除 {} 个 JEG 原始配方（由 Windymixin 数据包接管）", removed);
        }
    }
}
