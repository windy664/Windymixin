package org.windy.windymixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * 神秘农业配方削弱 — 直接修改原配方的count值。
 *
 * 不删除，不替换，只改数字。
 * 和JegRecipeFilter不同：JEG是删原版+数据包替代，MA是直接改原版。
 */
@EventBusSubscriber(modid = Windymixin.MODID)
public class MaRecipeNerf {

    private static final String MA = "mysticalagriculture";

    /** 精华→材料count削弱 */
    private static final Map<String, Integer> ESSENCE_NERF = Map.ofEntries(
            Map.entry("iron_ingot", 4),
            Map.entry("gold_ingot", 3),
            Map.entry("copper_ingot", 4),
            Map.entry("netherite_ingot", 1),
            Map.entry("coal", 8),
            Map.entry("lapis_lazuli", 8),
            Map.entry("redstone", 8),
            Map.entry("quartz", 8),
            Map.entry("diamond", 1),
            Map.entry("emerald", 1),
            Map.entry("glowstone_dust", 4),
            Map.entry("gunpowder", 4),
            Map.entry("bone", 4),
            Map.entry("string", 4),
            Map.entry("leather", 2),
            Map.entry("feather", 4),
            Map.entry("ender_pearl", 1),
            Map.entry("blaze_rod", 1),
            Map.entry("ghast_tear", 1),
            Map.entry("phantom_membrane", 1),
            Map.entry("wheat", 8),
            Map.entry("potato", 8),
            Map.entry("carrot", 8),
            Map.entry("beetroot", 8),
            Map.entry("melon", 8),
            Map.entry("pumpkin", 4),
            Map.entry("oak_log", 8),
            Map.entry("spruce_log", 8),
            Map.entry("birch_log", 8),
            Map.entry("cobblestone", 8),
            Map.entry("stone", 8),
            Map.entry("sand", 8),
            Map.entry("dirt", 8)
    );

    @SubscribeEvent
    static void onModifyRecipes(ModifyRecipeJsonsEvent event) {
        if (Config.CONFIG.ma_nerf == null || !Config.CONFIG.ma_nerf.enabled) return;

        Map<Identifier, JsonElement> recipes = event.getRecipeJsons();
        int essenceNerfed = 0;
        int reprocessorNerfed = 0;
        int totalMA = 0;

        for (Map.Entry<Identifier, JsonElement> entry : recipes.entrySet()) {
            Identifier id = entry.getKey();
            if (!MA.equals(id.getNamespace())) continue;

            totalMA++;
            JsonElement json = entry.getValue();
            if (!json.isJsonObject()) continue;
            JsonObject obj = json.getAsJsonObject();

            String path = id.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);

            // 精华→材料配方：修改result.count
            if (path.startsWith("essence/")) {
                Integer nerfedCount = ESSENCE_NERF.get(fileName);
                if (nerfedCount == null && path.startsWith("essence/common/")) {
                    nerfedCount = 1;
                }
                if (nerfedCount != null) {
                    if (modifyResultCount(obj, nerfedCount)) {
                        essenceNerfed++;
                    }
                }
                continue;
            }

            // 种子再处理机：count=2→1
            if (path.startsWith("seed/reprocessor/")) {
                if (modifyResultCount(obj, 1)) {
                    reprocessorNerfed++;
                }
                continue;
            }
        }

        Windymixin.LOGGER.info("[Windymixin] MA配方总数: {}, 削弱精华: {}, 削弱再处理机: {}", totalMA, essenceNerfed, reprocessorNerfed);
    }

    private static boolean modifyResultCount(JsonObject obj, int newCount) {
        if (!obj.has("result")) return false;
        JsonElement result = obj.get("result");
        if (!result.isJsonObject()) return false;
        JsonObject resultObj = result.getAsJsonObject();
        if (!resultObj.has("count")) return false;
        int old = resultObj.get("count").getAsInt();
        if (old == newCount) return false;
        resultObj.addProperty("count", newCount);
        return true;
    }
}
