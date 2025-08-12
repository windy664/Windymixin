package org.windy.windymixin.mixin.Youer.LegendarySevenDayGift;

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

@Mixin(PluginFixManager.class)
public class LegendarySevenDayGiftMixin {
    @Inject(method = "injectPluginFix", at = @At("HEAD"), cancellable = true)
    private static void injectMyPatch(String plugin, String className, byte[] clazz, CallbackInfoReturnable<byte[]> cir) {
        if ("com.gyzer.sevendaygift.LegendarySevenDayGift".equals(className)) {

            cir.setReturnValue(patchLegendarySevenDayGift(clazz));
        }
    }

    private static byte[] patchLegendarySevenDayGift(byte[] basicClass) {
        ClassNode node = new ClassNode();
        new ClassReader(basicClass).accept(node, 0);

        for (MethodNode method : node.methods) {
            if (method.name.equals("BukkitVersionHigh") && method.desc.equals("()Z")) {
                InsnList toInject = new InsnList();

                toInject.add(new InsnNode(Opcodes.ICONST_1));
                toInject.add(new InsnNode(Opcodes.IRETURN));

                method.instructions = toInject;
                method.tryCatchBlocks.clear();
            }
        }
        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }
}
