#!/usr/bin/env python3
"""
generate_assets.py — SpecterRealm Core asset generator.

Reads the same material/part definitions used by the Java code and writes:
  - assets/specterrealm/models/item/<name>.json     (one per item)
  - assets/specterrealm/lang/en_us.json             (all display names)
  - data/specterrealm/tags/item/crafting_parts.json (all part items)
  - data/specterrealm/tags/item/base_ingots.json    (all base ingots)
  - data/c/tags/item/<category>/<material>.json     (convention tags)

Run from the repo root:
  python3 scripts/generate_assets.py
"""

import json
import os
from pathlib import Path

# ── Paths ─────────────────────────────────────────────────────────────────────
REPO_ROOT = Path(__file__).parent.parent
ASSETS     = REPO_ROOT / "src/main/resources/assets/specterrealm"
DATA_SR    = REPO_ROOT / "src/main/resources/data/specterrealm"
DATA_C     = REPO_ROOT / "src/main/resources/data/c"

MODELS_DIR = ASSETS / "models/item"
LANG_FILE  = ASSETS / "lang/en_us.json"
TAGS_SR    = DATA_SR / "tags/item"
TAGS_C     = DATA_C / "tags/item"

# ── Materials ─────────────────────────────────────────────────────────────────
MATERIALS = [
    "iron", "copper", "gold", "tin", "lead", "steel", "osmium",
    "brass", "zinc", "silver", "bronze", "aluminum",
    "obsidian", "diamond", "netherite", "wood", "stone", "rubber",
]

def display_name(s):
    """'iron_spur_gear' → 'Iron Spur Gear'"""
    return " ".join(w.capitalize() for w in s.split("_"))

# ── Material sets (mirror of MaterialSets.java) ───────────────────────────────
COMMON_METALS      = {"iron","copper","gold","tin","lead","steel","osmium","brass","zinc","silver","bronze","aluminum","diamond","netherite"}
ALL_SOLID          = COMMON_METALS | {"obsidian","wood","stone"}
HARD_METALS        = {"iron","steel","brass","bronze","aluminum","silver","diamond","netherite","osmium","obsidian"}
SPRING_METALS      = {"iron","copper","gold","steel","brass","bronze","silver","osmium"}
VESSEL_METALS      = {"iron","copper","tin","lead","steel","brass","aluminum"}
CONDUCTIVE         = {"copper","gold","silver","osmium"}
BLADE_GRADE        = {"iron","steel","diamond"}
ABRASIVE           = {"stone","diamond"}
STRUCTURAL         = {"iron","steel","aluminum","brass","obsidian"}
GASKET_GRADE       = {"rubber","lead","copper"}
BATTERY_BASIC      = {"tin","lead"}
BATTERY_ADV        = {"osmium","gold"}
TRANSFORMER        = {"iron","steel"}
CAPACITOR_MATS     = {"copper","gold"}
BLANK_METALS       = {"iron","copper","gold","steel","brass","bronze","aluminum","silver","tin"}
TOOLING            = {"iron","steel","diamond"}
CRUCIBLE           = {"iron","steel","obsidian"}
HOSE_CLAMP_MATS    = {"iron","copper","steel"}
HANDLE_GRADE       = {"iron","copper","steel","brass","aluminum","wood","rubber"}
SWITCH_GRADE       = {"copper","gold","steel"}
BALL_BEARING_GRADE = {"iron","steel","diamond"}
HEAT_SINK_MATS     = {"iron","copper","aluminum","steel"}
NONE               = set()

# ── Part definitions (mirrors SpecterPart.java) ───────────────────────────────
# (id, category_id, material_prefix, materials_set)
PARTS = [
    # Mechanical
    ("spur_gear",        "mechanical",            True,  ALL_SOLID),
    ("bevel_gear",       "mechanical",            True,  COMMON_METALS),
    ("worm_gear",        "mechanical",            True,  COMMON_METALS),
    ("ring_gear",        "mechanical",            True,  COMMON_METALS),
    ("rack",             "mechanical",            True,  HARD_METALS),
    ("rod",              "mechanical",            True,  ALL_SOLID),
    ("screw",            "mechanical",            True,  COMMON_METALS),
    ("bolt",             "mechanical",            True,  COMMON_METALS),
    ("nut",              "mechanical",            True,  COMMON_METALS),
    ("spring",           "mechanical",            True,  SPRING_METALS),
    ("ring",             "mechanical",            True,  COMMON_METALS),
    ("bearing",          "mechanical",            True,  HARD_METALS),
    ("ball_bearing",     "mechanical",            True,  BALL_BEARING_GRADE),
    ("bushing",          "mechanical",            True,  VESSEL_METALS),
    ("coupling",         "mechanical",            True,  VESSEL_METALS),
    ("valve",            "mechanical",            True,  VESSEL_METALS),
    ("gasket",           "mechanical",            True,  GASKET_GRADE),
    ("piston_head",      "mechanical",            True,  VESSEL_METALS),
    ("nozzle",           "mechanical",            True,  HARD_METALS),
    ("handle",           "mechanical",            True,  HANDLE_GRADE),
    # Cutting / Grinding
    ("cutting_blade",    "cutting_grinding",      True,  BLADE_GRADE),
    ("saw_blade",        "cutting_grinding",      True,  BLADE_GRADE),
    ("grinding_wheel",   "cutting_grinding",      True,  ABRASIVE),
    ("drill_bit",        "cutting_grinding",      True,  BLADE_GRADE),
    # Sheet / Panel
    ("foil",             "sheet_panel",           True,  COMMON_METALS),
    ("plate",            "sheet_panel",           True,  ALL_SOLID),
    ("large_plate",      "sheet_panel",           True,  HARD_METALS),
    ("curved_plate",     "sheet_panel",           True,  VESSEL_METALS),
    ("panel",            "sheet_panel",           True,  COMMON_METALS),
    ("heat_sink",        "sheet_panel",           True,  HEAT_SINK_MATS),
    # Containers
    ("canister",         "containers",            True,  VESSEL_METALS),
    ("cylinder",         "containers",            True,  VESSEL_METALS),
    ("tank_fitting",     "containers",            True,  VESSEL_METALS),
    # Tubes / Hoses
    ("rubber_hose",      "tubes_hoses",           False, {"rubber"}),
    ("metal_tube",       "tubes_hoses",           True,  VESSEL_METALS),
    ("hose_clamp",       "tubes_hoses",           True,  HOSE_CLAMP_MATS),
    # Electrical
    ("wire_fine",        "electrical",            True,  CONDUCTIVE),
    ("wire_heavy",       "electrical",            True,  CONDUCTIVE),
    ("coil",             "electrical",            True,  CONDUCTIVE),
    ("em_coil",          "electrical",            False, NONE),
    ("transformer_core", "electrical",            True,  TRANSFORMER),
    ("capacitor",        "electrical",            True,  CAPACITOR_MATS),
    ("battery_cell",     "electrical",            True,  BATTERY_BASIC),
    ("battery_cell_adv", "electrical",            True,  BATTERY_ADV),
    ("led",              "electrical",            False, NONE),
    ("light_bulb",       "electrical",            False, NONE),
    ("switch",           "electrical",            True,  SWITCH_GRADE),
    # Computing
    ("resin_board",      "computing",             False, NONE),
    ("circuit_basic",    "computing",             False, NONE),
    ("circuit_advanced", "computing",             False, NONE),
    ("circuit_elite",    "computing",             False, NONE),
    ("processor",        "computing",             False, NONE),
    ("memory_module",    "computing",             False, NONE),
    ("logic_gate",       "computing",             False, NONE),
    ("gpu_basic",        "computing",             False, NONE),
    ("gpu_advanced",     "computing",             False, NONE),
    ("signal_antenna",   "computing",             False, NONE),
    # Structural
    ("frame",            "structural",            True,  STRUCTURAL),
    ("casing",           "structural",            True,  STRUCTURAL),
    ("bracket",          "structural",            True,  STRUCTURAL),
    ("duct",             "structural",            True,  STRUCTURAL),
    ("filter_element",   "structural",            True,  STRUCTURAL),
    # Machine Intermediates
    ("gear_blank",       "machine_intermediates", True,  BLANK_METALS),
    ("plate_blank",      "machine_intermediates", True,  BLANK_METALS),
    ("wire_draw_die",    "machine_intermediates", True,  TOOLING),
    ("stamping_die",     "machine_intermediates", True,  TOOLING),
    ("crucible_tray",    "machine_intermediates", True,  CRUCIBLE),
    ("mold",             "machine_intermediates", True,  TRANSFORMER),
]

# c: convention tag mapping: part_id → c: tag category (or None to skip)
C_TAG_MAP = {
    "spur_gear":    "gears",
    "bevel_gear":   "gears",
    "worm_gear":    "gears",
    "ring_gear":    "gears",
    "rack":         "gears",
    "rod":          "rods",
    "plate":        "plates",
    "large_plate":  "plates",
    "foil":         "foils",
    "wire_fine":    "wires",
    "wire_heavy":   "wires",
    "gear_blank":   "storage_blocks",  # not standard but useful
}

# ── Existing items (icons + field manual) — only need lang entries ────────────
EXISTING_LANG = {
    "item.specterrealm.field_manual":           "Field Manual",
    "item.specterrealm.icon_quest_gathering":   "Quest Icon: Gathering",
    "item.specterrealm.icon_quest_combat":      "Quest Icon: Combat",
    "item.specterrealm.icon_quest_exploration": "Quest Icon: Exploration",
    "item.specterrealm.icon_quest_crafting":    "Quest Icon: Crafting",
    "item.specterrealm.icon_quest_tech":        "Quest Icon: Tech",
    "item.specterrealm.icon_quest_magic":       "Quest Icon: The Veil",
    "item.specterrealm.icon_quest_boss":        "Quest Icon: Boss",
    "item.specterrealm.icon_quest_secret":      "Quest Icon: Restricted",
    "item.specterrealm.icon_quest_complete":    "Quest Icon: Complete",
    "item.specterrealm.icon_chapter_survive":   "Chapter Icon: Survive",
    "item.specterrealm.icon_chapter_establish": "Chapter Icon: Establish",
    "item.specterrealm.icon_chapter_automate":  "Chapter Icon: Automate",
    "item.specterrealm.icon_chapter_scale":     "Chapter Icon: Scale",
    "item.specterrealm.icon_chapter_command":   "Chapter Icon: Command",
    "itemGroup.specterrealm.main":              "SpecterRealm",
}

# ── Base ingots ───────────────────────────────────────────────────────────────
BASE_INGOT_MATS = ["silver", "bronze", "aluminum", "tin", "lead"]


def write_json(path: Path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    with open(path, "w") as f:
        json.dump(data, f, indent=2)
        f.write("\n")


def model_json(name):
    return {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"specterrealm:item/{name}"}
    }


def tag_json(*entries):
    return {"replace": False, "values": sorted(entries)}


def main():
    lang = dict(EXISTING_LANG)
    all_part_ids = []
    all_ingot_ids = []
    # c: tag buckets: {category: {material: [item_id, ...]}}
    c_tags = {}

    # ── Base ingots ───────────────────────────────────────────────────────────
    for mat in BASE_INGOT_MATS:
        name = f"{mat}_ingot"
        all_ingot_ids.append(f"specterrealm:{name}")
        lang[f"item.specterrealm.{name}"] = f"{display_name(mat)} Ingot"
        write_json(MODELS_DIR / f"{name}.json", model_json(name))
        # c: tag
        c_tags.setdefault("ingots", {}).setdefault(mat, []).append(f"specterrealm:{name}")

    # ── Crafting parts ────────────────────────────────────────────────────────
    for (part_id, _cat, mat_prefix, mats) in PARTS:
        if mat_prefix:
            for mat in sorted(mats):
                name = f"{mat}_{part_id}"
                all_part_ids.append(f"specterrealm:{name}")
                lang[f"item.specterrealm.{name}"] = display_name(name)
                write_json(MODELS_DIR / f"{name}.json", model_json(name))
                # c: convention tag if mapped
                c_cat = C_TAG_MAP.get(part_id)
                if c_cat:
                    c_tags.setdefault(c_cat, {}).setdefault(mat, []).append(f"specterrealm:{name}")
        else:
            name = part_id
            all_part_ids.append(f"specterrealm:{name}")
            lang[f"item.specterrealm.{name}"] = display_name(name)
            write_json(MODELS_DIR / f"{name}.json", model_json(name))

    # ── Write lang ────────────────────────────────────────────────────────────
    write_json(LANG_FILE, dict(sorted(lang.items())))

    # ── Write specterrealm grouping tags ─────────────────────────────────────
    write_json(TAGS_SR / "crafting_parts.json", tag_json(*all_part_ids))
    write_json(TAGS_SR / "base_ingots.json",    tag_json(*all_ingot_ids))

    # ── Write c: convention tags ──────────────────────────────────────────────
    for cat, mat_map in c_tags.items():
        for mat, items in mat_map.items():
            write_json(TAGS_C / cat / f"{mat}.json", tag_json(*items))

    # ── Summary ───────────────────────────────────────────────────────────────
    total = len(all_ingot_ids) + len(all_part_ids)
    print(f"Generated:")
    print(f"  {len(all_ingot_ids)} base ingot models + lang entries")
    print(f"  {len(all_part_ids)} part models + lang entries")
    print(f"  {total} items total")
    print(f"  {sum(len(v) for v in c_tags.values())} c: convention tag files")
    print(f"  lang file: {LANG_FILE}")
    print("Done.")


if __name__ == "__main__":
    main()
