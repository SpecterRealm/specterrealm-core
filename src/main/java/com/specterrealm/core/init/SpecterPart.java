package com.specterrealm.core.init;

import java.util.Set;

import static com.specterrealm.core.init.MaterialSets.*;
import static com.specterrealm.core.init.SpecterCategory.*;

/**
 * Every crafting part SpecterRealm registers.
 *
 * Each entry declares:
 *   id             — the part suffix used in item IDs
 *   category       — config category that gates this part
 *   materialPrefix — if true, items are registered as "{material}_{id}";
 *                    if false, the item is registered once as just "{id}"
 *   materials      — which materials are valid for this part;
 *                    empty = no material dependency (always registered if category is enabled)
 *
 * Display name formatting is handled by the asset generator (scripts/generate_assets.py).
 */
public enum SpecterPart {

    // ── Mechanical ────────────────────────────────────────────────────────────
    SPUR_GEAR      ("spur_gear",      MECHANICAL,            true,  ALL_SOLID),
    BEVEL_GEAR     ("bevel_gear",     MECHANICAL,            true,  COMMON_METALS),
    WORM_GEAR      ("worm_gear",      MECHANICAL,            true,  COMMON_METALS),
    RING_GEAR      ("ring_gear",      MECHANICAL,            true,  COMMON_METALS),
    RACK           ("rack",           MECHANICAL,            true,  HARD_METALS),
    ROD            ("rod",            MECHANICAL,            true,  ALL_SOLID),
    SCREW          ("screw",          MECHANICAL,            true,  COMMON_METALS),
    BOLT           ("bolt",           MECHANICAL,            true,  COMMON_METALS),
    NUT            ("nut",            MECHANICAL,            true,  COMMON_METALS),
    SPRING         ("spring",         MECHANICAL,            true,  SPRING_METALS),
    RING           ("ring",           MECHANICAL,            true,  COMMON_METALS),
    BEARING        ("bearing",        MECHANICAL,            true,  HARD_METALS),
    BALL_BEARING   ("ball_bearing",   MECHANICAL,            true,  BALL_BEARING_GRADE),
    BUSHING        ("bushing",        MECHANICAL,            true,  VESSEL_METALS),
    COUPLING       ("coupling",       MECHANICAL,            true,  VESSEL_METALS),
    VALVE          ("valve",          MECHANICAL,            true,  VESSEL_METALS),
    GASKET         ("gasket",         MECHANICAL,            true,  GASKET_GRADE),
    PISTON_HEAD    ("piston_head",    MECHANICAL,            true,  VESSEL_METALS),
    NOZZLE         ("nozzle",         MECHANICAL,            true,  HARD_METALS),
    HANDLE         ("handle",         MECHANICAL,            true,  HANDLE_GRADE),

    // ── Cutting / Grinding ────────────────────────────────────────────────────
    CUTTING_BLADE  ("cutting_blade",  CUTTING_GRINDING,      true,  BLADE_GRADE),
    SAW_BLADE      ("saw_blade",      CUTTING_GRINDING,      true,  BLADE_GRADE),
    GRINDING_WHEEL ("grinding_wheel", CUTTING_GRINDING,      true,  ABRASIVE),
    DRILL_BIT      ("drill_bit",      CUTTING_GRINDING,      true,  BLADE_GRADE),

    // ── Sheet / Panel ─────────────────────────────────────────────────────────
    FOIL           ("foil",           SHEET_PANEL,           true,  COMMON_METALS),
    PLATE          ("plate",          SHEET_PANEL,           true,  ALL_SOLID),
    LARGE_PLATE    ("large_plate",    SHEET_PANEL,           true,  HARD_METALS),
    CURVED_PLATE   ("curved_plate",   SHEET_PANEL,           true,  VESSEL_METALS),
    PANEL          ("panel",          SHEET_PANEL,           true,  COMMON_METALS),
    HEAT_SINK      ("heat_sink",      SHEET_PANEL,           true,  Set.of(SpecterMaterial.IRON, SpecterMaterial.COPPER, SpecterMaterial.ALUMINUM, SpecterMaterial.STEEL)),

    // ── Containers ────────────────────────────────────────────────────────────
    CANISTER       ("canister",       CONTAINERS,            true,  VESSEL_METALS),
    CYLINDER       ("cylinder",       CONTAINERS,            true,  VESSEL_METALS),
    TANK_FITTING   ("tank_fitting",   CONTAINERS,            true,  VESSEL_METALS),

    // ── Tubes / Hoses ─────────────────────────────────────────────────────────
    // rubber_hose has no material prefix — it is always rubber, named "rubber_hose"
    RUBBER_HOSE    ("rubber_hose",    TUBES_HOSES,           false, Set.of(SpecterMaterial.RUBBER)),
    METAL_TUBE     ("metal_tube",     TUBES_HOSES,           true,  VESSEL_METALS),
    HOSE_CLAMP     ("hose_clamp",     TUBES_HOSES,           true,  HOSE_CLAMP),

    // ── Electrical ────────────────────────────────────────────────────────────
    WIRE_FINE      ("wire_fine",      ELECTRICAL,            true,  CONDUCTIVE),
    WIRE_HEAVY     ("wire_heavy",     ELECTRICAL,            true,  CONDUCTIVE),
    COIL           ("coil",           ELECTRICAL,            true,  CONDUCTIVE),
    EM_COIL        ("em_coil",        ELECTRICAL,            false, NONE),
    TRANSFORMER_CORE ("transformer_core", ELECTRICAL,        true,  TRANSFORMER),
    CAPACITOR      ("capacitor",      ELECTRICAL,            true,  CAPACITOR),
    BATTERY_CELL   ("battery_cell",   ELECTRICAL,            true,  BATTERY_BASIC),
    BATTERY_CELL_ADV ("battery_cell_adv", ELECTRICAL,        true,  BATTERY_ADV),
    LED            ("led",            ELECTRICAL,            false, NONE),
    LIGHT_BULB     ("light_bulb",     ELECTRICAL,            false, NONE),
    SWITCH         ("switch",         ELECTRICAL,            true,  SWITCH_GRADE),

    // ── Computing ─────────────────────────────────────────────────────────────
    RESIN_BOARD    ("resin_board",    COMPUTING,             false, NONE),
    CIRCUIT_BASIC  ("circuit_basic",  COMPUTING,             false, NONE),
    CIRCUIT_ADV    ("circuit_advanced", COMPUTING,           false, NONE),
    CIRCUIT_ELITE  ("circuit_elite",  COMPUTING,             false, NONE),
    PROCESSOR      ("processor",      COMPUTING,             false, NONE),
    MEMORY_MODULE  ("memory_module",  COMPUTING,             false, NONE),
    LOGIC_GATE     ("logic_gate",     COMPUTING,             false, NONE),
    GPU_BASIC      ("gpu_basic",      COMPUTING,             false, NONE),
    GPU_ADV        ("gpu_advanced",   COMPUTING,             false, NONE),
    SIGNAL_ANTENNA ("signal_antenna", COMPUTING,             false, NONE),

    // ── Structural ────────────────────────────────────────────────────────────
    FRAME          ("frame",          STRUCTURAL,            true,  STRUCTURAL),
    CASING         ("casing",         STRUCTURAL,            true,  STRUCTURAL),
    BRACKET        ("bracket",        STRUCTURAL,            true,  STRUCTURAL),
    DUCT           ("duct",           STRUCTURAL,            true,  STRUCTURAL),
    FILTER_ELEMENT ("filter_element", STRUCTURAL,            true,  STRUCTURAL),

    // ── Machine Intermediates ─────────────────────────────────────────────────
    GEAR_BLANK     ("gear_blank",     MACHINE_INTERMEDIATES, true,  BLANK_METALS),
    PLATE_BLANK    ("plate_blank",    MACHINE_INTERMEDIATES, true,  BLANK_METALS),
    WIRE_DRAW_DIE  ("wire_draw_die",  MACHINE_INTERMEDIATES, true,  TOOLING),
    STAMPING_DIE   ("stamping_die",   MACHINE_INTERMEDIATES, true,  TOOLING),
    CRUCIBLE_TRAY  ("crucible_tray",  MACHINE_INTERMEDIATES, true,  CRUCIBLE),
    MOLD           ("mold",           MACHINE_INTERMEDIATES, true,  TRANSFORMER); // iron + steel

    // ─────────────────────────────────────────────────────────────────────────

    public final String id;
    public final SpecterCategory category;
    public final boolean materialPrefix;
    public final Set<SpecterMaterial> materials;

    SpecterPart(String id, SpecterCategory category, boolean materialPrefix, Set<SpecterMaterial> materials) {
        this.id = id;
        this.category = category;
        this.materialPrefix = materialPrefix;
        this.materials = materials;
    }

    /**
     * Returns the registry item ID for this part with the given material.
     * Only valid when materialPrefix is true.
     */
    public String itemId(SpecterMaterial material) {
        return material.id + "_" + id;
    }

    /**
     * Returns the single registry item ID for no-material-prefix parts.
     * Only valid when materialPrefix is false.
     */
    public String itemId() {
        return id;
    }

    public boolean supportsaterial(SpecterMaterial mat) {
        return materials.contains(mat);
    }

    /**
     * True if both this part's category and the given material are config-enabled,
     * and this part supports that material.
     */
    public boolean isEnabled(SpecterMaterial material) {
        return category.isEnabled() && material.isEnabled() && supportsaterial(material);
    }

    /**
     * True if this no-material-prefix part's category is enabled, and (if it has
     * material dependencies) at least one required material is enabled.
     */
    public boolean isEnabled() {
        if (!category.isEnabled()) return false;
        if (materials.isEmpty()) return true;
        return materials.stream().anyMatch(SpecterMaterial::isEnabled);
    }
}
