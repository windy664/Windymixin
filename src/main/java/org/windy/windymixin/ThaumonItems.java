package org.windy.windymixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 神秘时代主题物品注册 — 整合包 TC6 怀旧核心道具。
 *
 * 基于 Thaumon 素材，为整合包注入 Thaumcraft 氛围：
 * - 诱变剂（世界盐）：TC 经典消耗品，可用于合成/注魔
 */
public class ThaumonItems {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Windymixin.MODID);
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Windymixin.MODID);

    // ===== 神秘道具 =====
    /** 诱变剂 / 世界盐 — TC 经典消耗品 */
    public static final DeferredItem<Item> MUTAGEN = ITEMS.registerItem("mutagen", Item::new);

    // ===== 创造模式标签页 =====
    static {
        TABS.register("thaumon", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.windymixin.thaumon"))
                .icon(MUTAGEN.get()::getDefaultInstance)
                .displayItems((params, output) -> {
                    output.accept(MUTAGEN.get());
                })
                .build());
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
