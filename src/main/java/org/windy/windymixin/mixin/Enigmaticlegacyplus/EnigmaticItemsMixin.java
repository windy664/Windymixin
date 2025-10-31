package org.windy.windymixin.mixin.Enigmaticlegacyplus;

import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 服务端纯 Mixin 修复：
 * 在 EnigmaticItems 初始化之前动态替换危险物品的注册器。
 */
@Mixin(value = EnigmaticItems.class, remap = false)
public class EnigmaticItemsMixin {

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void prePatchBeforeInit(CallbackInfo ci) {
        // 仅在服务端执行
        if (FMLEnvironment.dist.isClient()) return;

        System.out.println("[EnigmaticFix] 正在服务端拦截部分客户端物品注册...");

        // 替换静态注册 lambda 为安全占位构造



        safeSupplier("THE_CUBE");
        safeSupplier("SOUL_COMPASS");
        safeSupplier("CHAOS_ELYTRA");
        safeSupplier("TOTEM_OF_MALICE");
    }

    /** 将某个物品注册字段替换为安全注册 Supplier<Item> 以避免加载客户端类 */
    private static void safeSupplier(String fieldName) {
        try {
            Field f = EnigmaticItems.class.getDeclaredField(fieldName);
            f.setAccessible(true);

            // 只有未初始化时才能安全替换
            Object current = f.get(null);
            if (current == null) {
                Supplier<Item> safe = () -> new Item(new Item.Properties());
                f.set(null, EnigmaticItems.ITEMS.register(fieldName.toLowerCase() + "_server_safe", safe));
                System.out.println("[EnigmaticFix] 跳过客户端物品: " + fieldName);
            }
        } catch (Throwable e) {
            System.err.println("[EnigmaticFix] 替换 " + fieldName + " 时出现问题: " + e);
        }
    }
}