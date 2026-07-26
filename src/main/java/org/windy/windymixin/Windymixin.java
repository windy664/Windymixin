package org.windy.windymixin;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.slf4j.Logger;
import org.windy.windymixin.compat.TomsStorageTerminal.TomsStorageTerminalSyncFix;
import org.windy.windymixin.mixin.TomsStorage.NetworkHandlerClientMixin;
import java.nio.file.Path;
import java.util.Optional;
import java.io.File;
import java.io.PrintWriter;

@Mod(Windymixin.MODID)
public class Windymixin {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "windymixin";
    private static String docname;
    private static String link;

    public Windymixin() {
        deployChaCoreStub();
        Config.load(); // 加载 windymixin.json
        LOGGER.info("[Windymixin] 模组初始化完成，JSON 配置系统已启动。");

        // 注册内置数据包覆盖其他mod配方
        IEventBus modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        if (modBus != null) {
            modBus.addListener(AddPackFindersEvent.class, event -> {
                if (event.getPackType() == PackType.SERVER_DATA) {
                    Path modRoot = ModList.get().getModFileById(MODID).getFile().getFilePath();
                    Path overridesPath = modRoot.resolve("windymixin_overrides");
                    event.addRepositorySource(consumer -> {
                        PackLocationInfo loc = new PackLocationInfo(
                                MODID + "_overrides",
                                Component.literal("Windymixin 配方魔改"),
                                PackSource.BUILT_IN,
                                Optional.empty()
                        );
                        PackSelectionConfig sel = new PackSelectionConfig(false, Pack.Position.TOP, false);
                        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
                            @Override
                            public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo info) {
                                return new PathPackResources(info, overridesPath);
                            }
                            @Override
                            public net.minecraft.server.packs.PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                                return new PathPackResources(info, overridesPath);
                            }
                        };
                        Pack pack = Pack.readMetaAndCreate(loc, supplier, PackType.SERVER_DATA, sel);
                        if (pack != null) consumer.accept(pack);
                    });
                }
            });
        }

        int mode = 1;
        if(mode==1){
            docname = "服务器文档.url";
            link = "https://docs.qq.com/aio/DQWpBaUFTeUtRQ2Js";
        } else if (mode==2) {
            docname = "服务器官网.url";
            link = "https://www.mcplay.cc/index.html";
        }

    }

    /**
     * 把 ChaCore stub jar 部署到服务器 libraries 目录，下次启动插件即可使用。
     */
    private static void deployChaCoreStub() {
        try {
            java.io.InputStream is = Windymixin.class.getResourceAsStream("/chacore-stub.jar");
            if (is == null) return;

            java.io.File libDir = new java.io.File("libraries");
            if (!libDir.exists()) libDir.mkdirs();

            java.io.File target = new java.io.File(libDir, "chacore-stub.jar");

            // 如果已存在且大小相同，跳过
            if (target.exists() && target.length() == is.available()) {
                is.close();
                LOGGER.info("[Windymixin] ChaCore stub jar already in libraries/");
                return;
            }

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(target)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            }
            is.close();
            LOGGER.info("[Windymixin] ✅ ChaCore stub jar deployed to libraries/chacore-stub.jar (restart to take effect)");
        } catch (Throwable t) {
            LOGGER.error("[Windymixin] ❌ Failed to deploy ChaCore stub: {}", t.getMessage());
        }
    }

    // 1.26 起 @EventBusSubscriber 不再有 bus 参数，框架按事件类型自动选总线
    @EventBusSubscriber(modid = Windymixin.MODID, value = Dist.CLIENT)
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

        @SubscribeEvent
        static void onClientTick(ClientTickEvent.Post event) {
            if (ModList.get().isLoaded("toms_storage")) {
                TomsStorageTerminalSyncFix.clientTick();
            }
        }
    }
}
