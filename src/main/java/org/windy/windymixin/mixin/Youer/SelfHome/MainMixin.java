//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.windy.windymixin.mixin.Youer.SelfHome;

import com.mohistmc.youer.bukkit.pluginfix.PluginFixManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PluginFixManager.class})
public abstract class MainMixin {
    @Inject(
            method = {"injectPluginFix"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private static void injectSelfHomeLog(String plugin, String className, byte[] clazz, CallbackInfoReturnable<byte[]> cir) {
        if ("com.SelfHome.Main".equals(className)) {
            ClassNode node = new ClassNode();
            (new ClassReader(clazz)).accept(node, 0);

            for(MethodNode method : node.methods) {
                if (method.name.equals("onEnable") && method.desc.equals("()V")) {
                    InsnList inject = new InsnList();
                    inject.add(new FieldInsnNode(178, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                    inject.add(new LdcInsnNode("成功被注入"));
                    inject.add(new MethodInsnNode(182, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                    method.instructions.insert(inject);
                }
            }

            ClassWriter writer = new ClassWriter(0);
            node.accept(writer);
            cir.setReturnValue(writer.toByteArray());
        }

    }
}
