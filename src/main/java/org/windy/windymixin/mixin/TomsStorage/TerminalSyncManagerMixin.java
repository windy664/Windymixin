package org.windy.windymixin.mixin.TomsStorage;

import com.tom.storagemod.inventory.StoredItemStack;
import com.tom.storagemod.util.TerminalSyncManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.windy.windymixin.Windymixin;

@Mixin(value = TerminalSyncManager.class, remap = false)
public class TerminalSyncManagerMixin {

    /**
     * 针对 TerminalSyncManager.read 中未判空直接调用的致命 Bug 进行兜底拦截。
     * 目标：idMap2.get(id)
     */
    @Redirect(
            method = "read",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;get(I)Ljava/lang/Object;",
                    remap = false
            ),
            remap = false
    )
    private Object windymixin$safeGet(Int2ObjectMap<StoredItemStack> instance, int id) {
        StoredItemStack stack = instance.get(id);

        // 如果因为网络包乱序导致缓存为空，绝不能返回 null 让它调 .getStack() 崩溃
        if (stack == null) {
            Windymixin.LOGGER.error("[Tom's Storage Fix] 网络包乱序：本地缓存缺少 id={} 的物品！已使用空物品兜底拦截崩溃。", id);
            // 这里调用 StoredItemStack(ItemStack, long) 构造器，非常安全
            return new StoredItemStack(ItemStack.EMPTY, 0);
        }

        return stack;
    }
}