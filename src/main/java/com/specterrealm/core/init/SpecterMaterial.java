package com.specterrealm.core.init;

import com.specterrealm.core.config.SpecterRealmConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

public enum SpecterMaterial {
    IRON      ("iron",       () -> SpecterRealmConfig.MATERIAL_IRON),
    COPPER    ("copper",     () -> SpecterRealmConfig.MATERIAL_COPPER),
    GOLD      ("gold",       () -> SpecterRealmConfig.MATERIAL_GOLD),
    TIN       ("tin",        () -> SpecterRealmConfig.MATERIAL_TIN),
    LEAD      ("lead",       () -> SpecterRealmConfig.MATERIAL_LEAD),
    STEEL     ("steel",      () -> SpecterRealmConfig.MATERIAL_STEEL),
    OSMIUM    ("osmium",     () -> SpecterRealmConfig.MATERIAL_OSMIUM),
    BRASS     ("brass",      () -> SpecterRealmConfig.MATERIAL_BRASS),
    ZINC      ("zinc",       () -> SpecterRealmConfig.MATERIAL_ZINC),
    SILVER    ("silver",     () -> SpecterRealmConfig.MATERIAL_SILVER),
    BRONZE    ("bronze",     () -> SpecterRealmConfig.MATERIAL_BRONZE),
    ALUMINUM  ("aluminum",   () -> SpecterRealmConfig.MATERIAL_ALUMINUM),
    OBSIDIAN  ("obsidian",   () -> SpecterRealmConfig.MATERIAL_OBSIDIAN),
    DIAMOND   ("diamond",    () -> SpecterRealmConfig.MATERIAL_DIAMOND),
    NETHERITE ("netherite",  () -> SpecterRealmConfig.MATERIAL_NETHERITE),
    WOOD      ("wood",       () -> SpecterRealmConfig.MATERIAL_WOOD),
    STONE     ("stone",      () -> SpecterRealmConfig.MATERIAL_STONE),
    RUBBER    ("rubber",     () -> SpecterRealmConfig.MATERIAL_RUBBER);

    public final String id;
    private final Supplier<ModConfigSpec.BooleanValue> configSupplier;

    SpecterMaterial(String id, Supplier<ModConfigSpec.BooleanValue> configSupplier) {
        this.id = id;
        this.configSupplier = configSupplier;
    }

    public boolean isEnabled() {
        return configSupplier.get().get();
    }

    public String displayName() {
        return Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }
}
