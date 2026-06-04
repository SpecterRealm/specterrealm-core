package com.specterrealm.core;

import com.specterrealm.core.config.SpecterRealmConfig;
import com.specterrealm.core.init.ItemInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(SpecterRealmCore.MODID)
public class SpecterRealmCore {

    public static final String MODID = "specterrealm";

    public SpecterRealmCore(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, SpecterRealmConfig.SPEC);

        ItemInit.ITEMS.register(modEventBus);
        ItemInit.TABS.register(modEventBus);
    }
}
