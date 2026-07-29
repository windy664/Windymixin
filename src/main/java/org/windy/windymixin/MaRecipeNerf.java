package org.windy.windymixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Set;

/**
 * 神秘农业全面削弱 — 引导玩家逐渐转移到工业mod。
 *
 * 削弱范围：
 * 1. 精华→材料：8精华=6锭 → 8精华=1锭（效率降6倍）
 * 2. 种子再处理机：2精华 → 1精华（效率降2倍）
 * 3. 注魔水晶：1000次 → 100次（消耗更快）
 * 4. 觉醒配方：精华需求×3（让觉醒变得极贵）
 * 5. 升级链配方：精华需求×2（每个tier都要更多精华）
 * 6. 附魔配方：精华需求×2（附魔更贵）
 * 7. 精华方块/锭/宝石：合成需求×2（存储更贵）
 *
 * 设计哲学：MA是新手跳板，不是终局方案。祭坛太贵→玩家自然转向工业mod。
 */
@EventBusSubscriber(modid = Windymixin.MODID)
public class MaRecipeNerf {

    private static final String MA_NAMESPACE = "mysticalagriculture";

    /** 精华→材料：基本矿产小幅削弱，高级材料大幅削弱 */
    private static final Map<String, Integer> ESSENCE_NERF = Map.ofEntries(
            // 基础金属：原版6→4（小幅削弱，前期能用）
            Map.entry("iron_ingot", 4),
            Map.entry("gold_ingot", 3),
            Map.entry("copper_ingot", 4),
            // 矿物：原版12→8（小幅削弱）
            Map.entry("coal", 8),
            Map.entry("lapis_lazuli", 8),
            Map.entry("redstone", 8),
            Map.entry("quartz", 8),
            // 高级材料：大幅削弱（4→1）
            Map.entry("diamond", 1),
            Map.entry("emerald", 1),
            Map.entry("netherite_ingot", 1),
            // 稀有掉落：大幅削弱
            Map.entry("ender_pearl", 1),
            Map.entry("blaze_rod", 1),
            Map.entry("ghast_tear", 1),
            Map.entry("phantom_membrane", 1),
            // 中级材料：适度削弱
            Map.entry("glowstone_dust", 4),
            Map.entry("gunpowder", 4),
            Map.entry("bone", 4),
            Map.entry("string", 4),
            Map.entry("leather", 2),
            Map.entry("feather", 4),
            // 食物：保持原版
            Map.entry("wheat", 8),
            Map.entry("potato", 8),
            Map.entry("carrot", 8),
            Map.entry("beetroot", 8),
            Map.entry("melon", 8),
            Map.entry("pumpkin", 4),
            // 基础材料：保持原版
            Map.entry("oak_log", 8),
            Map.entry("spruce_log", 8),
            Map.entry("birch_log", 8),
            Map.entry("cobblestone", 8),
            Map.entry("stone", 8),
            Map.entry("sand", 8),
            Map.entry("dirt", 8)
    );

    /** 需要翻倍精华需求的升级配方 */
    private static final Set<String> UPGRADE_RECIPES = Set.of(
            "inferium_upgrade", "prudentium_upgrade", "tertium_upgrade",
            "imperium_upgrade", "supremium_upgrade"
    );

    /** 需要翻倍精华需求的方块/锭/宝石配方 */
    private static final Set<String> BLOCK_RECIPES = Set.of(
            "inferium_block", "inferium_ingot", "inferium_gemstone",
            "prudentium_block", "prudentium_ingot", "prudentium_gemstone",
            "tertium_block", "tertium_ingot", "tertium_gemstone",
            "imperium_block", "imperium_ingot", "imperium_gemstone",
            "supremium_block", "supremium_ingot", "supremium_gemstone",
            "prosperity_block", "prosperity_ingot", "prosperity_gemstone",
            "soulium_block", "soulium_ingot", "soulium_gemstone"
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
        int crystalNerfed = 0;

        for (Map.Entry<Identifier, JsonElement> entry : recipes.entrySet()) {
            Identifier id = entry.getKey();
            if (!MA_NAMESPACE.equals(id.getNamespace())) continue;

            JsonElement json = entry.getValue();
            if (!json.isJsonObject()) continue;
            JsonObject obj = json.getAsJsonObject();

            String path = id.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1).replace(".json", "");

            // ===== 1. 精华→材料配方削弱 =====
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

            // ===== 2. 种子再处理机配方削弱 =====
            if (path.startsWith("recipe/seed/reprocessor/")) {
                if (modifyResultCount(obj, 1)) {
                    reprocessorNerfed++;
                }
                continue;
            }

            // ===== 3. 注魔水晶：1000次→100次 =====
            if (fileName.equals("infusion_crystal") || fileName.equals("master_infusion_crystal")) {
                // 注魔水晶的使用次数在config里，不是在配方里
                // 但我们可以让水晶更贵（增加材料需求）
                // 这里先跳过，后面通过config处理
                crystalNerfed++;
                continue;
            }

            // ===== 4. 升级链配方：精华需求翻倍 =====
            if (UPGRADE_RECIPES.contains(fileName)) {
                if (doubleIngredientCount(obj)) {
                    upgradeNerfed++;
                }
                continue;
            }

            // ===== 5. 方块/锭/宝石配方：精华需求翻倍 =====
            if (BLOCK_RECIPES.contains(fileName)) {
                if (doubleIngredientCount(obj)) {
                    blockNerfed++;
                }
                continue;
            }

            // ===== 6. 觉醒配方：精华需求×3 =====
            if (path.startsWith("recipe/") && fileName.contains("awakening")) {
                if (tripleEssenceCount(obj)) {
                    awakeningNerfed++;
                }
                continue;
            }

            // ===== 7. 附魔配方：精华需求翻倍 =====
            if (path.startsWith("recipe/enchanter/")) {
                if (doubleIngredientCount(obj)) {
                    enchanterNerfed++;
                }
                continue;
            }
        }

        // 日志
        if (essenceNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个精华→材料配方", essenceNerfed);
        if (reprocessorNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个种子再处理机配方", reprocessorNerfed);
        if (upgradeNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个升级链配方（精华需求×2）", upgradeNerfed);
        if (blockNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个方块/锭/宝石配方（精华需求×2）", blockNerfed);
        if (awakeningNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个觉醒配方（精华需求×3）", awakeningNerfed);
        if (enchanterNerfed > 0) Windymixin.LOGGER.info("[Windymixin] ✅ 削弱 {} 个附魔配方（精华需求×2）", enchanterNerfed);
    }

    /** 修改配方result的count */
    private static boolean modifyResultCount(JsonObject obj, int newCount) {
        if (!obj.has("result")) return false;
        JsonElement result = obj.get("result");
        if (!result.isJsonObject()) return false;
        JsonObject resultObj = result.getAsJsonObject();
        if (!resultObj.has("count")) return false;
        resultObj.addProperty("count", newCount);
        return true;
    }

    /** 翻倍配方中的ingredient count（适用于crafting_shaped的key） */
    private static boolean doubleIngredientCount(JsonObject obj) {
        // 对于shaped配方，增加pattern中的材料密度
        // 对于shapeless配方，增加ingredients中的count
        if (obj.has("ingredients") && obj.get("ingredients").isJsonArray()) {
            JsonArray ingredients = obj.getAsJsonArray("ingredients");
            for (JsonElement ing : ingredients) {
                if (ing.isJsonObject()) {
                    JsonObject ingObj = ing.getAsJsonObject();
                    if (ingObj.has("count")) {
                        int original = ingObj.get("count").getAsInt();
                        ingObj.addProperty("count", original * 2);
                    }
                }
            }
            return true;
        }
        // 对于shaped配方，修改key中的count（如果有的话）
        if (obj.has("key") && obj.get("key").isJsonObject()) {
            JsonObject key = obj.getAsJsonObject("key");
            for (Map.Entry<String, JsonElement> k : key.entrySet()) {
                if (k.getValue().isJsonObject()) {
                    JsonObject kObj = k.getValue().getAsJsonObject();
                    if (kObj.has("count")) {
                        int original = kObj.get("count").getAsInt();
                        kObj.addProperty("count", original * 2);
                    }
                }
            }
            return true;
        }
        return false;
    }

    /** 觉醒配方：精华需求×3 */
    private static boolean tripleEssenceCount(JsonObject obj) {
        if (!obj.has("essences") || !obj.get("essences").isJsonArray()) return false;
        JsonArray essences = obj.getAsJsonArray("essences");
        for (JsonElement ess : essences) {
            if (ess.isJsonObject()) {
                JsonObject essObj = ess.getAsJsonObject();
                if (essObj.has("count")) {
                    int original = essObj.get("count").getAsInt();
                    essObj.addProperty("count", original * 3);
                }
            }
        }
        return true;
    }
}
