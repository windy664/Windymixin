package org.windy.windymixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Windymixin JSON 配置系统
 * 文件路径： /config/windymixin.json
 */
public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger("WindymixinConfig");
    private static final Path CONFIG_PATH = Path.of("config", "windymixin.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** 当前配置对象 */
    public static Root CONFIG = new Root();

    /** 加载配置文件（若不存在则生成默认文件） */
    public static void load() {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                saveDefault();
                LOGGER.info("[Windymixin] 未找到 windymixin.json，已生成默认配置文件。");
            }
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                Root loaded = GSON.fromJson(reader, Root.class);
                // 合并缺失字段（兼容旧config）
                if (loaded.ma_nerf == null) loaded.ma_nerf = MaNerf.defaultMaNerf();
                CONFIG = loaded;
                LOGGER.info("[Windymixin] 成功加载配置 windymixin.json");
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error("[Windymixin] windymixin.json 格式错误: {}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("[Windymixin] 无法读取配置文件:", e);
        }
    }

    /** 写入默认配置文件 */
    private static void saveDefault() throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(Root.defaultConfig(), writer);
        }
    }

    /** 方块解析、容错系统 */
    public static Material parseBlockSafe(String id, String layerName) {
        if (id == null || id.isBlank()) {
            LOGGER.warn("[Windymixin] {} 未指定方块，默认为 AIR。", layerName);
            return Material.AIR;
        }
        try {
            String matName = id.replace("minecraft:", "").toUpperCase();
            Material mat = Material.getMaterial(matName);
            if (mat == null) {
                LOGGER.warn("[Windymixin] 未识别方块 '{}', [{}]，使用 AIR。", id, layerName);
                return Material.AIR;
            }
            return mat;
        } catch (Exception e) {
            LOGGER.warn("[Windymixin] 方块解析异常 '{}' [{}]，使用 AIR。", id, layerName);
            return Material.AIR;
        }
    }

    // ===== JSON 对应结构 =====

    /** 根对象 */
    public static class Root {
        public FlatWorld flat_world = new FlatWorld();
        public MaNerf ma_nerf = new MaNerf();

        public static Root defaultConfig() {
            Root root = new Root();
            root.flat_world = FlatWorld.defaultFlat();
            root.ma_nerf = MaNerf.defaultMaNerf();
            return root;
        }
    }

    /** 神秘农业削弱配置 */
    public static class MaNerf {
        /** 是否启用MA配方削弱 */
        public boolean enabled = true;
        /** 精华→锭的产出倍率（1.0=原版，0.17=8精华1锭） */
        public double output_multiplier = 0.17;
        /** 注魔水晶使用次数（原版1000） */
        public int infusion_crystal_uses = 100;
        /** 大师注魔水晶使用次数（原版-1=无限） */
        public int master_infusion_crystal_uses = 500;
        /** 升级链精华需求倍率（1.0=原版，2.0=翻倍） */
        public double upgrade_cost_multiplier = 2.0;
        /** 觉醒配方精华需求倍率（1.0=原版，3.0=三倍） */
        public double awakening_cost_multiplier = 3.0;
        /** 生长加速器tick间隔倍率（1.0=原版，6.0=60秒一次） */
        public double accelerator_slowdown = 6.0;

        public static MaNerf defaultMaNerf() {
            MaNerf m = new MaNerf();
            m.enabled = true;
            m.output_multiplier = 0.17;
            m.infusion_crystal_uses = 100;
            m.master_infusion_crystal_uses = 500;
            m.upgrade_cost_multiplier = 2.0;
            m.awakening_cost_multiplier = 3.0;
            m.accelerator_slowdown = 6.0;
            return m;
        }
    }

    /** 平坦世界配置 */
    public static class FlatWorld {
        public String biome = "plains";
        public List<Layer> layers = new ArrayList<>();
        public List<String> allowed_blocks = List.of("minecraft:bedrock", "minecraft:dirt", "minecraft:grass_block");

        public static FlatWorld defaultFlat() {
            FlatWorld f = new FlatWorld();
            f.biome = "plains";
            f.layers.add(new Layer("minecraft:bedrock", -64, -64));
            f.layers.add(new Layer("minecraft:dirt", -63, -2));
            f.layers.add(new Layer("minecraft:grass_block", -1, 1));
            return f;
        }
    }

    /** 每一层定义 */
    public static class Layer {
        public String block;
        public int from;
        public int to;

        public Layer() {}

        public Layer(String block, int from, int to) {
            this.block = block;
            this.from = from;
            this.to = to;
        }
    }
}