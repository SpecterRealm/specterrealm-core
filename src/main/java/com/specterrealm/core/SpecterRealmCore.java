package com.specterrealm.core;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(SpecterRealmCore.MODID)
public class SpecterRealmCore {
    public static final String MODID = "specterrealm";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

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

    public SpecterRealmCore(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<Item> registerIcon(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()
                .stacksTo(1)
                .fireResistant()));
    }
}
