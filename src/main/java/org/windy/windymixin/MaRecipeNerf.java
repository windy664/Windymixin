package org.windy.windymixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * 神秘农业配方拦截 — 删除原版精华配方，由Windymixin数据包提供削弱版。
 *
 * 工作原理：
 * 1. 在ModifyRecipeJsonsEvent中删除所有mysticalagriculture:recipe/essence/*配方
 * 2. 同时删除seed/reprocessor、升级链、觉醒、附魔等配方
 * 3. Windymixin数据包（data/windymixin/recipe/）提供削弱后的替代配方
 *
 * 这样玩家在JEI看到的直接就是削弱后的配方，原版完全消失。
 */
@EventBusSubscriber(modid = Windymixin.MODID)
public class MaRecipeNerf {

    private static final String MA = "mysticalagriculture";

    /** 需要删除的MA配方路径前缀 */
    private static final List<String> REMOVE_PREFIXES = List.of(
            "recipe/essence/",           // 所有精华→材料配方
            "recipe/seed/reprocessor/",  // 种子再处理机
            "recipe/enchanter/"          // 附魔配方
    );

    /** 需要删除的特定配方名（升级链、觉醒） */
    private static final Set<String> REMOVE_EXACT = Set.of(
            "inferium_upgrade", "prudentium_upgrade", "tertium_upgrade",
            "imperium_upgrade", "supremium_upgrade",
            "awakened_supremium_upgrade", "awakened_supremium_block_awakening"
    );

    @SubscribeEvent
    static void onModifyRecipes(ModifyRecipeJsonsEvent event) {
        if (!Config.CONFIG.ma_nerf.enabled) return;

        Map<Identifier, JsonElement> recipes = event.getRecipeJsons();
        Iterator<Map.Entry<Identifier, JsonElement>> it = recipes.entrySet().iterator();
        int removed = 0;

        while (it.hasNext()) {
            Map.Entry<Identifier, JsonElement> entry = it.next();
            Identifier id = entry.getKey();

            // 只处理mysticalagriculture命名空间
            if (!MA.equals(id.getNamespace())) continue;

            String path = id.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1).replace(".json", "");

            // 检查是否匹配删除条件
            boolean shouldRemove = false;

            // 前缀匹配
            for (String prefix : REMOVE_PREFIXES) {
                if (path.startsWith(prefix)) {
                    shouldRemove = true;
                    break;
                }
            }

            // 精确匹配
            if (!shouldRemove && REMOVE_EXACT.contains(fileName)) {
                shouldRemove = true;
            }

            if (shouldRemove) {
                it.remove();
                removed++;
            }
        }

        if (removed > 0) {
            Windymixin.LOGGER.info("[Windymixin] ✅ 已移除 {} 个神秘农业原版配方（由Windymixin数据包提供削弱版）", removed);
        }
    }
}
