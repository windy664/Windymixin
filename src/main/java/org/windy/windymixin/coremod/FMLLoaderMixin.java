package org.windy.windymixin.coremod;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.startup.StartupArgs;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * Hook FMLLoader.create，在 Instrumentation 初始化后注册 ClassFileTransformer，
 * 将旧 mod 中引用 ContainerScreenEvent$Render$Foreground 的字节码重写为 ScreenEvent$Render$Foreground。
 */
@Mixin(value = FMLLoader.class, remap = false)
public class FMLLoaderMixin {

    @Inject(method = "create(Ljava/lang/instrument/Instrumentation;Lnet/neoforged/fml/startup/StartupArgs;)Lnet/neoforged/fml/loading/FMLLoader;", at = @At("HEAD"))
    private static void windymixin$registerTransformer(
            Instrumentation instrumentation, StartupArgs args,
            CallbackInfoReturnable<FMLLoader> cir) {

        instrumentation.addTransformer(new ContainerScreenEventRedirectTransformer());
    }

    /**
     * ClassFileTransformer：在类加载时重写 ContainerScreenEvent$Render$Foreground 引用
     */
    private static class ContainerScreenEventRedirectTransformer implements ClassFileTransformer {

        private static final String OLD_FOREGROUND = "net/neoforged/neoforge/client/event/ContainerScreenEvent$Render$Foreground";
        private static final String NEW_FOREGROUND = "net/neoforged/neoforge/client/event/ScreenEvent$Render$Foreground";

        // 已知引用类
        private static final java.util.Set<String> TARGETS = java.util.Set.of(
                "net.p3pp3rf1y.sophisticatedcore.client.ClientEventHandler",
                "net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase",
                "net.p3pp3rf1y.sophisticatedcore.client.gui.SettingsScreen",
                "dev.architectury.event.neoforge.EventHandlerImplClient"
        );

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if (className == null || !TARGETS.contains(className)) {
                return null; // 不修改
            }

            // 检查是否真的包含旧引用
            String classContent = new String(classfileBuffer, java.nio.charset.StandardCharsets.ISO_8859_1);
            if (!classContent.contains(OLD_FOREGROUND)) {
                return null;
            }

            // 用 ASM 重写常量池
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            // ASM API version 9 = 9 << 16 = 589824
            ClassVisitor cv = new ClassVisitor(589824, cw) {
                @Override
                public void visit(int version, int access, String name, String signature,
                                  String superName, String[] interfaces) {
                    super.visit(version, access, name, replace(signature), replace(superName), interfaces);
                }

                @Override
                public org.objectweb.asm.FieldVisitor visitField(int access, String name, String descriptor,
                                                                  String signature, Object value) {
                    return super.visitField(access, name, replace(descriptor), replace(signature), value);
                }

                @Override
                public org.objectweb.asm.MethodVisitor visitMethod(int access, String name, String descriptor,
                                                                    String signature, String[] exceptions) {
                    return super.visitMethod(access, name, replace(descriptor), replace(signature), exceptions);
                }

                @Override
                public void visitInnerClass(String name, String outerName, String innerName, int access) {
                    super.visitInnerClass(replace(name), replace(outerName), innerName, access);
                }

                private String replace(String s) {
                    if (s == null) return null;
                    return s.replace(OLD_FOREGROUND, NEW_FOREGROUND);
                }
            };

            cr.accept(cv, ClassReader.SKIP_FRAMES);
            return cw.toByteArray();
        }
    }
}
