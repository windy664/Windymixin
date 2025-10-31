package org.windy.windymixin;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import java.io.File;
import java.io.PrintWriter;

@Mod(Windymixin.MODID)
public class Windymixin {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "windymixin";
    private static String docname;
    private static String link;

    public Windymixin() {
        Config.load(); // 加载 windymixin.json
        LOGGER.info("[Windymixin] 模组初始化完成，JSON 配置系统已启动。");
        int mode = 2;
        if(mode==1){
            docname = "服务器文档.url";
            link = "https://docs.qq.com/aio/DQWpBaUFTeUtRQ2Js";
        } else if (mode==2) {
            docname = "服务器官网.url";
            link = "https://www.mcplay.cc/index.html";
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

            File shortcutFile = new File(parentDir, docname);
           // File shortcutFile = new File(parentDir, "服务器官网.url");

            if (!shortcutFile.exists()) {
                try (PrintWriter writer = new PrintWriter(shortcutFile, "UTF-8")) {
                    writer.println("[{000214A0-0000-0000-C000-000000000046}]");
                    writer.println("Prop3=19,11");
                    writer.println("[InternetShortcut]");
                    writer.println("IDList=");
                    writer.println("URL="+link);

                } catch (Exception e) {
                    LOGGER.error("创建快捷方式失败", e);
                }
            }
        }
    }
}