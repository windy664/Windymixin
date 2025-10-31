package org.windy.windymixin.mixin.Youer.PluginFixManager.SelfHome;

import com.mohistmc.youer.bukkit.pluginfix.PluginFixManager;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for PluginFixManager: SelfHome 插件注入补丁
 *
 * 功能：
 * 在 com.SelfHome.Main#onEnable 中前置插入 System.out.println("成功被注入");
 * 用于验证 ASM 流程是否正常执行。
 */
@Mixin(PluginFixManager.class)
public abstract class MainMixin {

    @Inject(
            method = "injectPluginFix",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injectSelfHomeLog(String plugin, String className, byte[] clazz,
                                          CallbackInfoReturnable<byte[]> cir) {
        try {
            // 仅处理目标类
            if (!"com.SelfHome.Main".equals(className)) {
                return;
            }

            // ASM 解析原类
            ClassReader reader = new ClassReader(clazz);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);

            boolean injected = false;

            // 遍历所有方法
            for (MethodNode method : node.methods) {
                // 找到 public void onEnable()
                if ("onEnable".equals(method.name) && "()V".equals(method.desc)) {
                    if (method.instructions == null) continue;

                    // 创建一个指令列表（在方法头插入）
                    InsnList inject = new InsnList();
                    inject.add(new FieldInsnNode(Opcodes.GETSTATIC,
                            "java/lang/System",
                            "out",
                            "Ljava/io/PrintStream;"));

                    inject.add(new LdcInsnNode("成功被注入 SelfHome Main onEnable()"));
                    inject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                            "java/io/PrintStream",
                            "println",
                            "(Ljava/lang/String;)V",
                            false));

                    // 将注入插在方法最前
                    method.instructions.insert(inject);
                    injected = true;
                    break;
                }
            }

            // 写回字节码
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            byte[] modified = writer.toByteArray();

            // 设置返回值
            cir.setReturnValue(modified);
            cir.cancel();

            if (injected) {
                System.out.println("[YouerFix] ✅ SelfHome Main 类已成功注入 onEnable() 日志输出");
            } else {
                System.out.println("[YouerFix] ⚠ SelfHome Main 未找到 onEnable() 方法，未注入日志");
            }

        } catch (Throwable t) {
            System.err.println("[YouerFix] ❌ SelfHome Mixin 注入失败: " + t.getMessage());
            t.printStackTrace();
            // 若出错则直接返回原始字节码
            cir.setReturnValue(clazz);
            cir.cancel();
        }
    }
}