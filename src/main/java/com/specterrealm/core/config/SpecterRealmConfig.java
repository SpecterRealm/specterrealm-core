package com.specterrealm.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SpecterRealmConfig {

    public static final ModConfigSpec SPEC;

    // ── Materials ─────────────────────────────────────────────────────────────
    public static final ModConfigSpec.BooleanValue MATERIAL_IRON;
    public static final ModConfigSpec.BooleanValue MATERIAL_COPPER;
    public static final ModConfigSpec.BooleanValue MATERIAL_GOLD;
    public static final ModConfigSpec.BooleanValue MATERIAL_TIN;
    public static final ModConfigSpec.BooleanValue MATERIAL_LEAD;
    public static final ModConfigSpec.BooleanValue MATERIAL_STEEL;
    public static final ModConfigSpec.BooleanValue MATERIAL_OSMIUM;
    public static final ModConfigSpec.BooleanValue MATERIAL_BRASS;
    public static final ModConfigSpec.BooleanValue MATERIAL_ZINC;
    public static final ModConfigSpec.BooleanValue MATERIAL_SILVER;
    public static final ModConfigSpec.BooleanValue MATERIAL_BRONZE;
    public static final ModConfigSpec.BooleanValue MATERIAL_ALUMINUM;
    public static final ModConfigSpec.BooleanValue MATERIAL_OBSIDIAN;
    public static final ModConfigSpec.BooleanValue MATERIAL_DIAMOND;
    public static final ModConfigSpec.BooleanValue MATERIAL_NETHERITE;
    public static final ModConfigSpec.BooleanValue MATERIAL_WOOD;
    public static final ModConfigSpec.BooleanValue MATERIAL_STONE;
    public static final ModConfigSpec.BooleanValue MATERIAL_RUBBER;

    // ── Categories ────────────────────────────────────────────────────────────
    public static final ModConfigSpec.BooleanValue CATEGORY_MECHANICAL;
    public static final ModConfigSpec.BooleanValue CATEGORY_CUTTING_GRINDING;
    public static final ModConfigSpec.BooleanValue CATEGORY_SHEET_PANEL;
    public static final ModConfigSpec.BooleanValue CATEGORY_CONTAINERS;
    public static final ModConfigSpec.BooleanValue CATEGORY_TUBES_HOSES;
    public static final ModConfigSpec.BooleanValue CATEGORY_ELECTRICAL;
    public static final ModConfigSpec.BooleanValue CATEGORY_COMPUTING;
    public static final ModConfigSpec.BooleanValue CATEGORY_STRUCTURAL;
    public static final ModConfigSpec.BooleanValue CATEGORY_MACHINE_INTERMEDIATES;
    public static final ModConfigSpec.BooleanValue CATEGORY_BASE_MATERIALS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.comment(
            "Material toggles.",
            "Disabling a material removes ALL parts and items made from it.",
            "Items still exist in the registry but are hidden from creative tabs and JEI."
        ).push("materials");
        MATERIAL_IRON      = b.comment("Iron parts and items").define("iron", true);
        MATERIAL_COPPER    = b.comment("Copper parts and items").define("copper", true);
        MATERIAL_GOLD      = b.comment("Gold parts and items").define("gold", true);
        MATERIAL_TIN       = b.comment("Tin parts — sourced from Mekanism or SpecterRealm base materials").define("tin", true);
        MATERIAL_LEAD      = b.comment("Lead parts — sourced from Mekanism or SpecterRealm base materials").define("lead", true);
        MATERIAL_STEEL     = b.comment("Steel parts — sourced from Mekanism or another steel mod").define("steel", true);
        MATERIAL_OSMIUM    = b.comment("Osmium parts — sourced from Mekanism; disable if Mekanism is not present").define("osmium", false);
        MATERIAL_BRASS     = b.comment("Brass parts — sourced from Create or SpecterRealm base materials").define("brass", true);
        MATERIAL_ZINC      = b.comment("Zinc parts — sourced from Create; disable if Create is not present").define("zinc", false);
        MATERIAL_SILVER    = b.comment("Silver parts — SpecterRealm provides ingot if no other mod does").define("silver", true);
        MATERIAL_BRONZE    = b.comment("Bronze parts — SpecterRealm provides ingot if no other mod does").define("bronze", true);
        MATERIAL_ALUMINUM  = b.comment("Aluminum parts — SpecterRealm provides ingot if no other mod does").define("aluminum", true);
        MATERIAL_OBSIDIAN  = b.comment("Obsidian parts — heat and blast resistant tier").define("obsidian", true);
        MATERIAL_DIAMOND   = b.comment("Diamond parts — extreme tier").define("diamond", true);
        MATERIAL_NETHERITE = b.comment("Netherite parts — ultimate tier").define("netherite", true);
        MATERIAL_WOOD      = b.comment("Wood parts — primitive tier").define("wood", true);
        MATERIAL_STONE     = b.comment("Stone parts — primitive tier").define("stone", true);
        MATERIAL_RUBBER    = b.comment("Rubber parts — sourced from pack mod").define("rubber", true);
        b.pop();

        b.comment(
            "Category toggles.",
            "Disabling a category removes all parts in that group.",
            "Use this to hide entire sections irrelevant to your pack (e.g. disable computing for a magic pack)."
        ).push("categories");
        CATEGORY_MECHANICAL            = b.comment("Gears, rods, screws, springs, bearings, valves, etc.").define("mechanical", true);
        CATEGORY_CUTTING_GRINDING      = b.comment("Saw blades, cutting blades, grinding wheels, drill bits").define("cutting_grinding", true);
        CATEGORY_SHEET_PANEL           = b.comment("Foil, plates, large plates, curved plates, panels, heat sinks").define("sheet_panel", true);
        CATEGORY_CONTAINERS            = b.comment("Canisters, cylinders, tank fittings").define("containers", true);
        CATEGORY_TUBES_HOSES           = b.comment("Rubber hose, metal tubes, hose clamps").define("tubes_hoses", true);
        CATEGORY_ELECTRICAL            = b.comment("Wire, coils, capacitors, batteries, LEDs").define("electrical", true);
        CATEGORY_COMPUTING             = b.comment("Circuit boards, processors, memory, logic gates, GPUs, antennas").define("computing", true);
        CATEGORY_STRUCTURAL            = b.comment("Frames, casings, brackets, ducts, filter elements").define("structural", true);
        CATEGORY_MACHINE_INTERMEDIATES = b.comment("Machine-gated blanks and tooling: gear blanks, plate blanks, dies, molds").define("machine_intermediates", true);
        CATEGORY_BASE_MATERIALS        = b.comment("SpecterRealm-native ingots (silver, bronze, aluminum, tin, lead) for packs without a source mod").define("base_materials", true);
        b.pop();

        SPEC = b.build();
    }
}
