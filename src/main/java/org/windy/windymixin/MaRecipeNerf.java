package org.windy.windymixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * 神秘农业配方拦截 — 删除原版，由Windymixin数据包接管。
 *
 * 原理和JegRecipeFilter一样：
 * 1. 在ModifyRecipeJsonsEvent中删除MA原版配方
 * 2. Windymixin数据包（data/windymixin/recipe/ma/）提供削弱版配方
 */
@EventBusSubscriber(modid = Windymixin.MODID)
public class MaRecipeNerf {

    private static final String MA = "mysticalagriculture";

    /** 需要删除的路径前缀 */
    private static final List<String> REMOVE_PREFIXES = List.of(
            "recipe/essence/",
            "recipe/seed/reprocessor/",
            "recipe/enchanter/"
    );

    /** 需要删除的精确匹配 */
    private static final Set<String> REMOVE_EXACT = Set.of(
            "inferium_upgrade", "prudentium_upgrade", "tertium_upgrade",
            "imperium_upgrade", "supremium_upgrade",
            "awakened_supremium_upgrade",
            "awakened_supremium_block_awakening"
    );

    @SubscribeEvent
    static void onModifyRecipes(ModifyRecipeJsonsEvent event) {
        if (!Config.CONFIG.ma_nerf.enabled) return;

        Map<Identifier, JsonElement> recipes = event.getRecipeJsons();
        Iterator<Map.Entry<Identifier, JsonElement>> it = recipes.entrySet().iterator();
        int removed = 0;

        while (it.hasNext()) {
            Identifier id = it.next().getKey();
            if (!MA.equals(id.getNamespace())) continue;

            String path = id.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1).replace(".json", "");

            boolean shouldRemove = false;
            for (String prefix : REMOVE_PREFIXES) {
                if (path.startsWith(prefix)) { shouldRemove = true; break; }
            }
            if (!shouldRemove && REMOVE_EXACT.contains(fileName)) {
                shouldRemove = true;
            }

            if (shouldRemove) {
                it.remove();
                removed++;
            }
        }

        if (removed > 0) {
            Windymixin.LOGGER.info("[Windymixin] ✅ 已移除 {} 个MA原版配方（由Windymixin数据包接管）", removed);
        }
    }
}
