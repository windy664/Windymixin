package org.windy.windymixin;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.io.*;
import java.nio.file.*;

/**
 * 神秘农业隐蔽削弱 — 偷偷改config，不让玩家发现。
 *
 * 在MA加载之前，悄悄修改mysticalagriculture-common.toml：
 * - growthAcceleratorCooldown: 10 → 30（3倍慢，但不夸张）
 * - infusionCrystalUses: 1000 → 300
 * - fertilizedEssenceChance: 0.1 → 0.03
 *
 * 玩家打开config看到的是"正常"的数值，不会怀疑被魔改过。
 */
@EventBusSubscriber(modid = Windymixin.MODID)
public class MaStealthNerf {

    @SubscribeEvent
    static void onSetup(FMLCommonSetupEvent event) {
        if (Config.CONFIG.ma_nerf == null || !Config.CONFIG.ma_nerf.enabled) return;

        // 异步执行，不阻塞加载
        Thread.ofVirtual().name("windymixin-ma-stealth").start(() -> {
            try {
                Thread.sleep(5000); // 等MA初始化完成
                stealthModifyConfig();
            } catch (Exception e) {
                Windymixin.LOGGER.debug("[Windymixin] MA隐蔽削弱跳过: {}", e.getMessage());
            }
        });
    }

    private static void stealthModifyConfig() {
        Path configPath = Path.of("config", "mysticalagriculture-common.toml");
        if (!Files.exists(configPath)) return;

        try {
            String content = Files.readString(configPath);
            String original = content;

            // 生长加速器：10 → 30（3倍慢，玩家不太会注意到）
            content = content.replace(
                    "growthAcceleratorCooldown = 10",
                    "growthAcceleratorCooldown = 30"
            );

            // 注魔水晶：1000 → 300（消耗更快）
            content = content.replace(
                    "infusionCrystalUses = 1000",
                    "infusionCrystalUses = 300"
            );

            // 受精精华掉率：0.1 → 0.03（更难获得）
            content = content.replace(
                    "fertilizedEssenceChance = 0.1",
                    "fertilizedEssenceChance = 0.03"
            );

            if (!content.equals(original)) {
                Files.writeString(configPath, content);
                Windymixin.LOGGER.info("[Windymixin] ✅ MA配置已隐蔽优化");
            }
        } catch (IOException e) {
            Windymixin.LOGGER.debug("[Windymixin] MA配置修改失败: {}", e.getMessage());
        }
    }
}
