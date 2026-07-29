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
 * 基于 Thaumon 素材，为整合包注入 Thaumcraft 氛围。
 * 建材方块（Greatwood/Silverwood/Ancient Stone 等）不在此注册，
 * 由 Thaumon-Assets 资源包提供贴图，玩家可搭配其他模组使用。
 */
public class ThaumonItems {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Windymixin.MODID);
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Windymixin.MODID);

    // ===== 神秘道具 =====
    /** 诱变剂 / 世界盐 — TC 经典消耗品 */
    public static final DeferredItem<Item> MUTAGEN         = ITEMS.registerItem("mutagen", Item::new);
    /** 魔典 — 神秘之书，研究手册 */
    public static final DeferredItem<Item> GRIMOIRE        = ITEMS.registerItem("grimoire", Item::new);
    /** 魔典堆 — 装饰 / 合成材料 */
    public static final DeferredItem<Item> GRIMOIRE_STACK  = ITEMS.registerItem("grimoire_stack", Item::new);
    /** 研究笔记 — 解锁配方 / 研究点 */
    public static final DeferredItem<Item> RESEARCH_NOTES  = ITEMS.registerItem("research_notes", Item::new);
    /** 炼金蒸馏器 — 注魔台 / 蒸馏台物品 */
    public static final DeferredItem<Item> RETORT          = ITEMS.registerItem("retort", Item::new);
    /** 玻璃瓶架 — 源质瓶架 */
    public static final DeferredItem<Item> VIAL_RACK       = ITEMS.registerItem("vial_rack", Item::new);
    /** 女巫炼药锅 — 蒸馏锅 */
    public static final DeferredItem<Item> WITCH_CAULDRON  = ITEMS.registerItem("witch_cauldron", Item::new);

    // ===== 创造模式标签页 =====
    static {
        TABS.register("thaumon", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.windymixin.thaumon"))
                .icon(MUTAGEN.get()::getDefaultInstance)
                .displayItems((params, output) -> {
                    output.accept(MUTAGEN.get());
                    output.accept(GRIMOIRE.get());
                    output.accept(GRIMOIRE_STACK.get());
                    output.accept(RESEARCH_NOTES.get());
                    output.accept(RETORT.get());
                    output.accept(VIAL_RACK.get());
                    output.accept(WITCH_CAULDRON.get());
                })
                .build());
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
