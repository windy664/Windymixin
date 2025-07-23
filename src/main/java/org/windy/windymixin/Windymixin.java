package org.windy.windymixin;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

import java.io.File;
import java.io.PrintWriter;

import static net.neoforged.neoforge.common.NeoForge.EVENT_BUS;

@Mod(Windymixin.MODID)
public class Windymixin {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "windymixin";

    public Windymixin() {
        // 将事件监听器注册到 Forge 事件总线
        EVENT_BUS.register(this);

    }

    // 玩家登录事件监听
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.literal("§e欢迎来到未来之旅！QQ群：965811912"));
        }
    }

    @EventBusSubscriber(modid = Windymixin.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            File currentDir = new File(System.getProperty("user.dir"));
            File parentDir = currentDir.getParentFile();
            if (parentDir == null) {
                return;
            }
            // 快捷方式文件名
            File shortcutFile = new File(parentDir, "服务器文档.url");

            if (!shortcutFile.exists()) {
                try (PrintWriter writer = new PrintWriter(shortcutFile, "UTF-8")) {
                    writer.println("[{000214A0-0000-0000-C000-000000000046}]");
                    writer.println("Prop3=19,11");
                    writer.println("[InternetShortcut]");
                    writer.println("IDList=");
                    writer.println("URL=https://docs.qq.com/aio/DQWpBaUFTeUtRQ2Js");
                    LOGGER.info("已创建快捷方式：{}", shortcutFile.getAbsolutePath(),"欢迎加入服务器QQ群：965811912");
                } catch (Exception e) {
                    LOGGER.error("创建快捷方式失败", e);
                }
            }
        }
    }
}