package org.windy.windymixin.coremod;

import net.neoforged.neoforgespi.transformation.ClassProcessor;
import net.neoforged.neoforgespi.transformation.ProcessorName;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;

/**
 * 动态生成被 NeoForge 26.2.0.40-beta 删除的 ContainerScreenEvent 类及其内部类。
 * 通过 ClassProcessorProvider SPI 在类加载阶段注入字节码，避免 JPMS split-package 冲突。
 */
public class ContainerScreenEventProcessor implements ClassProcessor {

    private static final String PKG = "net/neoforged/neoforge/client/event";

    private static final String CONTAINER_SCREEN_EVENT = PKG + "/ContainerScreenEvent";
    private static final String RENDER = PKG + "/ContainerScreenEvent$Render";
    private static final String FOREGROUND = PKG + "/ContainerScreenEvent$Render$Foreground";

    // Java 17 class file version = (17 << 16) | 0 = 0x003B0000
    private static final int JAVA_17 = 65537 + (16 << 16);

    private static final Set<String> HANDLED_CLASSES = Set.of(
            CONTAINER_SCREEN_EVENT,
            RENDER,
            FOREGROUND
    );

    @Override
    public ProcessorName name() {
        return new ProcessorName("windymixin", "container_screen_event_compat");
    }

    @Override
    public Set<String> generatesPackages() {
        return Set.of(PKG.replace('/', '.'));
    }

    @Override
    public boolean handlesClass(SelectionContext context) {
        return HANDLED_CLASSES.contains(context.type().getInternalName());
    }

    @Override
    public ComputeFlags processClass(TransformationContext context) {
        if (!context.empty()) {
            return ComputeFlags.NO_REWRITE;
        }

        String internalName = context.type().getInternalName();
        ClassNode node = context.node();

        switch (internalName) {
            case CONTAINER_SCREEN_EVENT -> generateContainerScreenEvent(node);
            case RENDER -> generateRender(node);
            case FOREGROUND -> generateForeground(node);
            default -> {
                return ComputeFlags.NO_REWRITE;
            }
        }

        return ComputeFlags.COMPUTE_FRAMES;
    }

    /**
     * ContainerScreenEvent extends Event
     */
    private void generateContainerScreenEvent(ClassNode node) {
        node.version = JAVA_17;
        node.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT;
        node.name = CONTAINER_SCREEN_EVENT;
        node.superName = "net/neoforged/bus/api/Event";

        // 字段
        node.visitField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "containerScreen",
                "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;",
                null, null
        );

        // 构造器
        var mv = node.visitMethod(
                Opcodes.ACC_PROTECTED, "<init>",
                "(Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;)V",
                null, null
        );
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/neoforged/bus/api/Event", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, CONTAINER_SCREEN_EVENT, "containerScreen",
                "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);

        // getContainerScreen()
        mv = node.visitMethod(
                Opcodes.ACC_PUBLIC, "getContainerScreen",
                "()Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;",
                null, null
        );
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, CONTAINER_SCREEN_EVENT, "containerScreen",
                "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
    }

    /**
     * ContainerScreenEvent$Render extends ContainerScreenEvent
     */
    private void generateRender(ClassNode node) {
        node.version = JAVA_17;
        node.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_STATIC;
        node.name = RENDER;
        node.superName = CONTAINER_SCREEN_EVENT;

        // 字段
        node.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "guiGraphics",
                "Lnet/minecraft/client/gui/GuiGraphicsExtractor;", null, null);
        node.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "mouseX", "I", null, null);
        node.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "mouseY", "I", null, null);

        // 构造器
        var mv = node.visitMethod(
                Opcodes.ACC_PROTECTED, "<init>",
                "(Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
                null, null
        );
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, CONTAINER_SCREEN_EVENT, "<init>",
                "(Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;)V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitFieldInsn(Opcodes.PUTFIELD, RENDER, "guiGraphics",
                "Lnet/minecraft/client/gui/GuiGraphicsExtractor;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ILOAD, 3);
        mv.visitFieldInsn(Opcodes.PUTFIELD, RENDER, "mouseX", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ILOAD, 4);
        mv.visitFieldInsn(Opcodes.PUTFIELD, RENDER, "mouseY", "I");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 5);

        // getGuiGraphics()
        mv = node.visitMethod(Opcodes.ACC_PUBLIC, "getGuiGraphics",
                "()Lnet/minecraft/client/gui/GuiGraphicsExtractor;", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, RENDER, "guiGraphics",
                "Lnet/minecraft/client/gui/GuiGraphicsExtractor;");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);

        // getMouseX()
        mv = node.visitMethod(Opcodes.ACC_PUBLIC, "getMouseX", "()I", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, RENDER, "mouseX", "I");
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);

        // getMouseY()
        mv = node.visitMethod(Opcodes.ACC_PUBLIC, "getMouseY", "()I", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, RENDER, "mouseY", "I");
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);
    }

    /**
     * ContainerScreenEvent$Render$Foreground extends ContainerScreenEvent$Render
     */
    private void generateForeground(ClassNode node) {
        node.version = JAVA_17;
        node.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
        node.name = FOREGROUND;
        node.superName = RENDER;

        // 构造器
        var mv = node.visitMethod(
                Opcodes.ACC_PUBLIC, "<init>",
                "(Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
                null, null
        );
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitVarInsn(Opcodes.ILOAD, 3);
        mv.visitVarInsn(Opcodes.ILOAD, 4);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, RENDER, "<init>",
                "(Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(5, 5);
    }
}
