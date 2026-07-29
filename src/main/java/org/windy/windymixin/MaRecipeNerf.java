package org.windy.windymixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * 神秘农业配方削弱 — 直接修改原配方的count值。
 *
 * 不删除原配方，只改result.count。
 * 这样玩家在JEI看到的是削弱后的配方，原版数量消失。
 */
@EventBusSubscriber(modid = Windymixin.MODID)
public class MaRecipeNerf {

    private static final String MA = "mysticalagriculture";

    /** 精华→材料：基本矿产小幅削弱，高级材料大幅削弱 */
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
        if (!Config.CONFIG.ma_nerf.enabled) return;

        Map<Identifier, JsonElement> recipes = event.getRecipeJsons();
        int essenceNerfed = 0;
        int reprocessorNerfed = 0;
        int upgradeNerfed = 0;
        int blockNerfed = 0;
        int awakeningNerfed = 0;
        int enchanterNerfed = 0;

        for (Map.Entry<Identifier, JsonElement> entry : recipes.entrySet()) {
            Identifier id = entry.getKey();
            if (!MA.equals(id.getNamespace())) continue;

            JsonElement json = entry.getValue();
            if (!json.isJsonObject()) continue;
            JsonObject obj = json.getAsJsonObject();

            String path = id.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1).replace(".json", "");

            // 精华→材料配方：修改result.count
            if (path.startsWith("recipe/essence/")) {
                Integer nerfedCount = ESSENCE_NERF.get(fileName);
                if (nerfedCount == null && path.startsWith("recipe/essence/common/")) {
                    nerfedCount = 1;
                }
                if (nerfedCount != null && modifyResultCount(obj, nerfedCount)) {
                    essenceNerfed++;
                }
                continue;
            }

            // 种子再处理机：2→1
            if (path.startsWith("recipe/seed/reprocessor/")) {
                if (modifyResultCount(obj, 1)) {
                    reprocessorNerfed++;
                }
                continue;
            }

            // 升级链：ingredients count翻倍
            if (fileName.contains("upgrade") && !fileName.contains("awakened")) {
                if (doubleIngredients(obj)) {
                    upgradeNerfed++;
                }
                continue;
            }

            // 方块/锭/宝石：ingredients count翻倍
            if (fileName.contains("_block") || fileName.contains("_ingot") || fileName.contains("_gemstone")) {
                if (path.startsWith("recipe/") && !path.startsWith("recipe/essence/") && !path.startsWith("recipe/seed/")) {
                    if (doubleIngredients(obj)) {
                        blockNerfed++;
                    }
                }
                continue;
            }

            // 觉醒配方：essences count×3
            if (path.startsWith("recipe/") && fileName.contains("awakening")) {
                if (tripleEssences(obj)) {
                    awakeningNerfed++;
                }
                continue;
            }

            // 附魔配方：ingredients count翻倍
            if (path.startsWith("recipe/enchanter/")) {
                if (doubleIngredients(obj)) {
                    enchanterNerfed++;
                }
                continue;
            }
        }

        if (essenceNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个精华配方", essenceNerfed);
        if (reprocessorNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个再处理机配方", reprocessorNerfed);
        if (upgradeNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个升级配方", upgradeNerfed);
        if (blockNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个方块/锭配方", blockNerfed);
        if (awakeningNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个觉醒配方", awakeningNerfed);
        if (enchanterNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个附魔配方", enchanterNerfed);
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

    private static boolean doubleIngredients(JsonObject obj) {
        boolean changed = false;
        if (obj.has("ingredients") && obj.get("ingredients").isJsonArray()) {
            for (JsonElement ing : obj.getAsJsonArray("ingredients")) {
                if (ing.isJsonObject()) {
                    JsonObject o = ing.getAsJsonObject();
                    if (o.has("count")) {
                        o.addProperty("count", o.get("count").getAsInt() * 2);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private static boolean tripleEssences(JsonObject obj) {
        if (!obj.has("essences") || !obj.get("essences").isJsonArray()) return false;
        for (JsonElement ess : obj.getAsJsonArray("essences")) {
            if (ess.isJsonObject()) {
                JsonObject o = ess.getAsJsonObject();
                if (o.has("count")) {
                    o.addProperty("count", o.get("count").getAsInt() * 3);
                }
            }
        }
        return true;
    }
}
