package com.specterrealm.core.init;

import com.specterrealm.core.SpecterRealmCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ItemInit {

    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(SpecterRealmCore.MODID);

    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpecterRealmCore.MODID);

    // ── Icon items (quest / chapter markers for FTB Quests / Patchouli) ──────
    public static final DeferredItem<Item> ICON_QUEST_GATHERING   = registerIcon("icon_quest_gathering");
    public static final DeferredItem<Item> ICON_QUEST_COMBAT      = registerIcon("icon_quest_combat");
    public static final DeferredItem<Item> ICON_QUEST_EXPLORATION = registerIcon("icon_quest_exploration");
    public static final DeferredItem<Item> ICON_QUEST_CRAFTING    = registerIcon("icon_quest_crafting");
    public static final DeferredItem<Item> ICON_QUEST_TECH        = registerIcon("icon_quest_tech");
    public static final DeferredItem<Item> ICON_QUEST_MAGIC       = registerIcon("icon_quest_magic");
    public static final DeferredItem<Item> ICON_QUEST_BOSS        = registerIcon("icon_quest_boss");
    public static final DeferredItem<Item> ICON_QUEST_SECRET      = registerIcon("icon_quest_secret");
    public static final DeferredItem<Item> ICON_QUEST_COMPLETE    = registerIcon("icon_quest_complete");
    public static final DeferredItem<Item> ICON_CHAPTER_SURVIVE   = registerIcon("icon_chapter_survive");
    public static final DeferredItem<Item> ICON_CHAPTER_ESTABLISH = registerIcon("icon_chapter_establish");
    public static final DeferredItem<Item> ICON_CHAPTER_AUTOMATE  = registerIcon("icon_chapter_automate");
    public static final DeferredItem<Item> ICON_CHAPTER_SCALE     = registerIcon("icon_chapter_scale");
    public static final DeferredItem<Item> ICON_CHAPTER_COMMAND   = registerIcon("icon_chapter_command");

    // ── Base material ingots (SpecterRealm-native; disabled if another mod ───
    // provides them and the pack dev turns this category off)
    private static final Map<String, DeferredItem<Item>> BASE_INGOTS;

    static {
        Map<String, DeferredItem<Item>> ingots = new LinkedHashMap<>();
        for (String mat : new String[]{ "silver", "bronze", "aluminum", "tin", "lead" }) {
            String name = mat + "_ingot";
            ingots.put(name, ITEMS.register(name, () -> new Item(new Item.Properties())));
        }
        BASE_INGOTS = Collections.unmodifiableMap(ingots);
    }

    // ── Crafting parts (data-driven; all registered, config gates visibility) ─
    private static final Map<String, DeferredItem<Item>> PARTS;

    static {
        Map<String, DeferredItem<Item>> parts = new LinkedHashMap<>();

        for (SpecterPart part : SpecterPart.values()) {
            if (part.materialPrefix) {
                // Register one item per valid material: {material}_{partId}
                for (SpecterMaterial mat : part.materials) {
                    String name = part.itemId(mat);
                    parts.put(name, ITEMS.register(name, () -> new Item(new Item.Properties())));
                }
            } else {
                // Register exactly one item with no material prefix: {partId}
                String name = part.itemId();
                parts.put(name, ITEMS.register(name, () -> new Item(new Item.Properties())));
            }
        }

        PARTS = Collections.unmodifiableMap(parts);
    }

    // ── Creative tab ─────────────────────────────────────────────────────────
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPECTERREALM_TAB =
        TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.specterrealm.main"))
            .icon(() -> PARTS.getOrDefault("iron_spur_gear",
                                           ICON_QUEST_TECH).get().getDefaultInstance())
            .displayItems((params, output) -> {
                // Icons — always shown
                output.accept(ICON_QUEST_GATHERING);
                output.accept(ICON_QUEST_COMBAT);
                output.accept(ICON_QUEST_EXPLORATION);
                output.accept(ICON_QUEST_CRAFTING);
                output.accept(ICON_QUEST_TECH);
                output.accept(ICON_QUEST_MAGIC);
                output.accept(ICON_QUEST_BOSS);
                output.accept(ICON_QUEST_SECRET);
                output.accept(ICON_QUEST_COMPLETE);
                output.accept(ICON_CHAPTER_SURVIVE);
                output.accept(ICON_CHAPTER_ESTABLISH);
                output.accept(ICON_CHAPTER_AUTOMATE);
                output.accept(ICON_CHAPTER_SCALE);
                output.accept(ICON_CHAPTER_COMMAND);

                // Base ingots — gated by BASE_MATERIALS category and material config
                if (com.specterrealm.core.config.SpecterRealmConfig.CATEGORY_BASE_MATERIALS.get()) {
                    for (Map.Entry<String, DeferredItem<Item>> e : BASE_INGOTS.entrySet()) {
                        // Derive material name from "silver_ingot" → "silver"
                        String mat = e.getKey().replace("_ingot", "");
                        SpecterMaterial sm = materialByid(mat);
                        if (sm == null || sm.isEnabled()) {
                            output.accept(e.getValue());
                        }
                    }
                }

                // Crafting parts — gated by both category and material config
                for (SpecterPart part : SpecterPart.values()) {
                    if (part.materialPrefix) {
                        for (SpecterMaterial mat : part.materials) {
                            if (part.isEnabled(mat)) {
                                DeferredItem<Item> item = PARTS.get(part.itemId(mat));
                                if (item != null) output.accept(item);
                            }
                        }
                    } else {
                        if (part.isEnabled()) {
                            DeferredItem<Item> item = PARTS.get(part.itemId());
                            if (item != null) output.accept(item);
                        }
                    }
                }
            })
            .build());

    // ── Helpers ───────────────────────────────────────────────────────────────

    public static Map<String, DeferredItem<Item>> getParts() {
        return PARTS;
    }

    public static Map<String, DeferredItem<Item>> getBaseIngots() {
        return BASE_INGOTS;
    }

    private static DeferredItem<Item> registerIcon(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()
            .stacksTo(1)
            .fireResistant()));
    }

    private static SpecterMaterial materialByid(String id) {
        for (SpecterMaterial m : SpecterMaterial.values()) {
            if (m.id.equals(id)) return m;
        }
        return null;
    }
}
