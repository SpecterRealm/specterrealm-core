package com.specterrealm.core.init;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static com.specterrealm.core.init.SpecterMaterial.*;

/**
 * Named material groupings used by SpecterPart to declare which materials
 * each part type supports. Defined in a companion class so they can be
 * used in SpecterPart enum constructor args (enum static fields initialize
 * after enum constants, making in-enum definitions unusable there).
 */
public final class MaterialSets {

    private MaterialSets() {}

    /** All common metals — no primitives, no rubber. */
    public static final Set<SpecterMaterial> COMMON_METALS = Collections.unmodifiableSet(EnumSet.of(
        IRON, COPPER, GOLD, TIN, LEAD, STEEL, OSMIUM, BRASS, ZINC, SILVER, BRONZE, ALUMINUM, DIAMOND, NETHERITE
    ));

    /** All solid materials — metals + stone + wood + obsidian, no rubber. */
    public static final Set<SpecterMaterial> ALL_SOLID = Collections.unmodifiableSet(EnumSet.of(
        IRON, COPPER, GOLD, TIN, LEAD, STEEL, OSMIUM, BRASS, ZINC, SILVER, BRONZE, ALUMINUM,
        OBSIDIAN, DIAMOND, NETHERITE, WOOD, STONE
    ));

    /** Harder structural metals suitable for precision machined parts. */
    public static final Set<SpecterMaterial> HARD_METALS = Collections.unmodifiableSet(EnumSet.of(
        IRON, STEEL, BRASS, BRONZE, ALUMINUM, SILVER, DIAMOND, NETHERITE, OSMIUM, OBSIDIAN
    ));

    /** Spring-grade metals — must be elastic and not too brittle. */
    public static final Set<SpecterMaterial> SPRING_METALS = Collections.unmodifiableSet(EnumSet.of(
        IRON, COPPER, GOLD, STEEL, BRASS, BRONZE, SILVER, OSMIUM
    ));

    /** Metals suitable for pressure vessels and fluid-handling components. */
    public static final Set<SpecterMaterial> VESSEL_METALS = Collections.unmodifiableSet(EnumSet.of(
        IRON, COPPER, TIN, LEAD, STEEL, BRASS, ALUMINUM
    ));

    /** Electrically conductive materials for wire and coil parts. */
    public static final Set<SpecterMaterial> CONDUCTIVE = Collections.unmodifiableSet(EnumSet.of(
        COPPER, GOLD, SILVER, OSMIUM
    ));

    /** Cutting and blade-grade materials. */
    public static final Set<SpecterMaterial> BLADE_GRADE = Collections.unmodifiableSet(EnumSet.of(
        IRON, STEEL, DIAMOND
    ));

    /** Abrasive materials for grinding wheels. */
    public static final Set<SpecterMaterial> ABRASIVE = Collections.unmodifiableSet(EnumSet.of(
        STONE, DIAMOND
    ));

    /** Structural frame and casing materials. */
    public static final Set<SpecterMaterial> STRUCTURAL = Collections.unmodifiableSet(EnumSet.of(
        IRON, STEEL, ALUMINUM, BRASS, OBSIDIAN
    ));

    /** Gasket materials — soft enough to seal. */
    public static final Set<SpecterMaterial> GASKET_GRADE = Collections.unmodifiableSet(EnumSet.of(
        RUBBER, LEAD, COPPER
    ));

    /** Battery chemistry materials — basic cell. */
    public static final Set<SpecterMaterial> BATTERY_BASIC = Collections.unmodifiableSet(EnumSet.of(
        TIN, LEAD
    ));

    /** Battery chemistry materials — advanced cell. */
    public static final Set<SpecterMaterial> BATTERY_ADV = Collections.unmodifiableSet(EnumSet.of(
        OSMIUM, GOLD
    ));

    /** Transformer core materials — magnetically permeable metals. */
    public static final Set<SpecterMaterial> TRANSFORMER = Collections.unmodifiableSet(EnumSet.of(
        IRON, STEEL
    ));

    /** Capacitor conductor materials. */
    public static final Set<SpecterMaterial> CAPACITOR = Collections.unmodifiableSet(EnumSet.of(
        COPPER, GOLD
    ));

    /** Machine blank / casting metals. */
    public static final Set<SpecterMaterial> BLANK_METALS = Collections.unmodifiableSet(EnumSet.of(
        IRON, COPPER, GOLD, STEEL, BRASS, BRONZE, ALUMINUM, SILVER, TIN
    ));

    /** Die and tooling materials. */
    public static final Set<SpecterMaterial> TOOLING = Collections.unmodifiableSet(EnumSet.of(
        IRON, STEEL, DIAMOND
    ));

    /** Crucible / tray materials — heat resistant. */
    public static final Set<SpecterMaterial> CRUCIBLE = Collections.unmodifiableSet(EnumSet.of(
        IRON, STEEL, OBSIDIAN
    ));

    /** Hose clamp metals. */
    public static final Set<SpecterMaterial> HOSE_CLAMP = Collections.unmodifiableSet(EnumSet.of(
        IRON, COPPER, STEEL
    ));

    /** Handle materials — grips need to be practical to hold. */
    public static final Set<SpecterMaterial> HANDLE_GRADE = Collections.unmodifiableSet(EnumSet.of(
        IRON, COPPER, STEEL, BRASS, ALUMINUM, WOOD, RUBBER
    ));

    /** Switch materials — conductive + structural. */
    public static final Set<SpecterMaterial> SWITCH_GRADE = Collections.unmodifiableSet(EnumSet.of(
        COPPER, GOLD, STEEL
    ));

    /** Ball bearing materials — must be very hard and smooth. */
    public static final Set<SpecterMaterial> BALL_BEARING_GRADE = Collections.unmodifiableSet(EnumSet.of(
        IRON, STEEL, DIAMOND
    ));

    /** Empty — used for parts with no material variants (computing items, etc.). */
    public static final Set<SpecterMaterial> NONE = Collections.emptySet();
}
