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
                CONFIG = GSON.fromJson(reader, Root.class);
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

        public static Root defaultConfig() {
            Root root = new Root();
            root.flat_world = FlatWorld.defaultFlat();
            return root;
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