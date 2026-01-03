package org.windy.windymixin.mixin.FTBQuests;

import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.FTBQuests;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.reflect.Field;
import java.nio.file.Path;

@Mixin(ServerQuestFile.class)
public abstract class ServerQuestFileMixin {

    /**
     * 反射获取 ServerQuestFile 的私有 folder 字段
     */
    private static Field getFolderField() throws NoSuchFieldException {
        return ServerQuestFile.class.getDeclaredField("folder");
    }

    /**
     * 注入到 getFolder() 方法，确保 folder 初始化
     */
    @Inject(
            method = "getFolder",
            at = @At("HEAD"), // 在原方法执行前注入
            cancellable = true // 允许覆盖返回值
    )
    private void onGetFolder(CallbackInfoReturnable<Path> cir) {
        try {
            // 获取当前 Mixin 代理的 ServerQuestFile 实例
            ServerQuestFile original = (ServerQuestFile) (Object) this;

            Field folderField = getFolderField();
            folderField.setAccessible(true); // 强制设置可访问

            // 检查 folder 是否为 null
            Path folder = (Path) folderField.get(original);
            if (folder == null) {
                FTBQuests.LOGGER.info("FTB Quests 文件夹未初始化，自动尝试加载...");
                original.load(true, true);
                folder = (Path) folderField.get(original);
            }

            // 返回非 null 的 folder 路径
            cir.setReturnValue(folder);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            FTBQuests.LOGGER.error("反射访问 FTB Quests folder 失败", e);
            cir.setReturnValue(null);
        }
    }
}