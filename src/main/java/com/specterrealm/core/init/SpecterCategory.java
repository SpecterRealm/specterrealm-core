package com.specterrealm.core.init;

import com.specterrealm.core.config.SpecterRealmConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

public enum SpecterCategory {
    MECHANICAL            ("mechanical",            () -> SpecterRealmConfig.CATEGORY_MECHANICAL),
    CUTTING_GRINDING      ("cutting_grinding",      () -> SpecterRealmConfig.CATEGORY_CUTTING_GRINDING),
    SHEET_PANEL           ("sheet_panel",           () -> SpecterRealmConfig.CATEGORY_SHEET_PANEL),
    CONTAINERS            ("containers",            () -> SpecterRealmConfig.CATEGORY_CONTAINERS),
    TUBES_HOSES           ("tubes_hoses",           () -> SpecterRealmConfig.CATEGORY_TUBES_HOSES),
    ELECTRICAL            ("electrical",            () -> SpecterRealmConfig.CATEGORY_ELECTRICAL),
    COMPUTING             ("computing",             () -> SpecterRealmConfig.CATEGORY_COMPUTING),
    STRUCTURAL            ("structural",            () -> SpecterRealmConfig.CATEGORY_STRUCTURAL),
    MACHINE_INTERMEDIATES ("machine_intermediates", () -> SpecterRealmConfig.CATEGORY_MACHINE_INTERMEDIATES),
    BASE_MATERIALS        ("base_materials",        () -> SpecterRealmConfig.CATEGORY_BASE_MATERIALS);

    public final String id;
    private final Supplier<ModConfigSpec.BooleanValue> configSupplier;

    SpecterCategory(String id, Supplier<ModConfigSpec.BooleanValue> configSupplier) {
        this.id = id;
        this.configSupplier = configSupplier;
    }

    public boolean isEnabled() {
        return configSupplier.get().get();
    }
}
