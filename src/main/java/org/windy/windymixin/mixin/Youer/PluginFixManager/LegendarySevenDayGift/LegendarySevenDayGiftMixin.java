package org.windy.windymixin.mixin.Youer.PluginFixManager.LegendarySevenDayGift;

import com.mohistmc.youer.bukkit.pluginfix.PluginFixManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin 补丁：为 LegendarySevenDayGift 修复 Bukkit 版本兼容问题。
 *
 * 功能：
 *  - 监听 PluginFixManager.injectPluginFix()
 *  - 当 className 为 com.gyzer.sevendaygift.LegendarySevenDayGift 时
 *    修改 BukkitVersionHigh() 为始终返回 true
 */
@Mixin(PluginFixManager.class)
public abstract class LegendarySevenDayGiftMixin {

    /**
     * 注入 PluginFixManager.injectPluginFix() 的最开始位置，
     * 如果检测到目标类，则中断原逻辑并返回修补后的字节码。
     */
    @Inject(
            method = "injectPluginFix",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injectLegendaryGiftPatch(String plugin, String className, byte[] clazz,
                                                 CallbackInfoReturnable<byte[]> cir) {
        try {
            // 判断目标插件类名
            if ("com.gyzer.sevendaygift.LegendarySevenDayGift".equals(className)) {
                cir.setReturnValue(patchLegendarySevenDayGift(clazz));
                cir.cancel(); // 阻止继续进入 PluginFixManager 原本逻辑
            }
        } catch (Throwable t) {
            t.printStackTrace();
            // 防止 Mixin 异常导致 PluginFixManager 加载失败
        }
    }

    /**
     * 实际补丁逻辑：
     * 将 BukkitVersionHigh() 修改为永远返回 true。
     *
     * 此类方法通常用于插件检测 "Bukkit 是否高版本",
     * 在 1.20+、1.21+ 上很多插件旧代码会误判。
     */
    private static byte[] patchLegendarySevenDayGift(byte[] basicClass) {
        try {
            ClassReader reader = new ClassReader(basicClass);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);

            for (MethodNode method : node.methods) {
                // 寻找 boolean BukkitVersionHigh()
                if ("BukkitVersionHigh".equals(method.name) && "()Z".equals(method.desc)) {
                    InsnList toInject = new InsnList();

                    // 直接返回 true
                    toInject.add(new InsnNode(Opcodes.ICONST_1));
                    toInject.add(new InsnNode(Opcodes.IRETURN));

                    method.instructions.clear();
                    method.instructions.add(toInject);
                    method.tryCatchBlocks.clear();
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            return writer.toByteArray();

        } catch (Throwable ex) {
            ex.printStackTrace();
            // 如果补丁失败，fallback 返回原始类
            return basicClass;
        }
    }
}