package org.windy.windymixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 枪械零件物品注册 — 14 个自定义材料，用于 JEG 配方分层魔改。
 *
 * T1（工业起步）: steel_pipe, screws, hexagonal_nails, pig_iron, iron_castings
 * T2（火力进阶）: polymer_layer, plastic, polyester_cloth, smokeless_powder, hemp_seeds
 * T3（终局材料）: tree_oil, wooden_heart, twig_buds, paw
 */
public class GunPartItems {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Windymixin.MODID);
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Windymixin.MODID);

    // ===== T1 工业起步 =====
    public static final DeferredItem<Item> STEEL_PIPE       = ITEMS.registerItem("steel_pipe", Item::new);
    public static final DeferredItem<Item> SCREWS           = ITEMS.registerItem("screws", Item::new);
    public static final DeferredItem<Item> HEXAGONAL_NAILS  = ITEMS.registerItem("hexagonal_nails", Item::new);
    public static final DeferredItem<Item> PIG_IRON         = ITEMS.registerItem("pig_iron", Item::new);
    public static final DeferredItem<Item> IRON_CASTINGS    = ITEMS.registerItem("iron_castings", Item::new);

    // ===== T2 火力进阶 =====
    public static final DeferredItem<Item> POLYMER_LAYER    = ITEMS.registerItem("polymer_layer", Item::new);
    public static final DeferredItem<Item> PLASTIC          = ITEMS.registerItem("plastic", Item::new);
    public static final DeferredItem<Item> POLYESTER_CLOTH  = ITEMS.registerItem("polyester_cloth", Item::new);
    public static final DeferredItem<Item> SMOKELESS_POWDER = ITEMS.registerItem("smokeless_powder", Item::new);
    public static final DeferredItem<Item> HEMP_SEEDS       = ITEMS.registerItem("hemp_seeds", Item::new);

    // ===== T3 终局材料 =====
    public static final DeferredItem<Item> TREE_OIL         = ITEMS.registerItem("tree_oil", Item::new);
    public static final DeferredItem<Item> WOODEN_HEART     = ITEMS.registerItem("wooden_heart", Item::new);
    public static final DeferredItem<Item> TWIG_BUDS        = ITEMS.registerItem("twig_buds", Item::new);
    public static final DeferredItem<Item> PAW              = ITEMS.registerItem("paw", Item::new);

    // ===== 创造模式标签页 =====
    static {
        TABS.register("gun_parts", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.windymixin.gun_parts"))
                .icon(STEEL_PIPE.get()::getDefaultInstance)
                .displayItems((params, output) -> {
                    output.accept(STEEL_PIPE.get());
                    output.accept(SCREWS.get());
                    output.accept(HEXAGONAL_NAILS.get());
                    output.accept(PIG_IRON.get());
                    output.accept(IRON_CASTINGS.get());
                    output.accept(POLYMER_LAYER.get());
                    output.accept(PLASTIC.get());
                    output.accept(POLYESTER_CLOTH.get());
                    output.accept(SMOKELESS_POWDER.get());
                    output.accept(HEMP_SEEDS.get());
                    output.accept(TREE_OIL.get());
                    output.accept(WOODEN_HEART.get());
                    output.accept(TWIG_BUDS.get());
                    output.accept(PAW.get());
                })
                .build());
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
