package org.windy.windymixin.mixin.Youer;

import com.mohistmc.youer.api.WorldAPI;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.windy.windymixin.Config;

import java.util.Random;

/**
 * 动态 JSON 可配置版超平坦生成器
 */
@Mixin(value = WorldAPI.FlatGenerator.class, remap = false)
public abstract class WorldAPIMixin extends ChunkGenerator {

    protected WorldAPIMixin() {}

    /**
     * 替换原始 FlatGenerator 方法
     */
    @Overwrite
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        ChunkData chunkData = this.createChunkData(world);

        var cfg = Config.CONFIG.flat_world;

        // 解析生物群系
        Biome biomeSelected;
        try {
            biomeSelected = Biome.valueOf(cfg.biome.toUpperCase());
        } catch (IllegalArgumentException e) {
            biomeSelected = Biome.PLAINS;
        }

        // 为整个区块设置生物群系
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                biome.setBiome(i, j, biomeSelected);
            }
        }

        // 遍历每个层级
        for (Config.Layer layer : cfg.layers) {
            Material block = Config.parseBlockSafe(layer.block, "地层");
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int y = layer.from; y <= layer.to; y++) {
                        chunkData.setBlock(i, y, j, block);
                    }
                }
            }
        }

        // 设置顶部空气层
        int topY = cfg.layers.stream().mapToInt(l -> l.to).max().orElse(1);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int y = topY + 1; y < world.getMaxHeight(); y++) {
                    chunkData.setBlock(i, y, j, Material.AIR);
                }
            }
        }
        System.out.println(String.format(
                "风吟的Mixin发力了：[World: %s, Random: %s, X: %d, Z: %d, BiomeGrid: %s, SelectedBiome: %s]",
                world.getName(),
                random.toString(),
                x,
                z,
                biome.toString(),
                biomeSelected.name()
        ));

        return chunkData;
    }
}