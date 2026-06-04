# SpecterRealm Core — AI Context

## What This Is

NeoForge 1.21.1 library mod (`mod_id: specterrealm`) that provides shared assets, items, and infrastructure for Colony Protocol modpacks. Not a standalone gameplay mod — it fills gaps that vanilla and other mods don't cover cleanly, especially for recipe gating and crafting component standardization.

**Required dependency:** Patchouli (loaded AFTER specterrealm)

## Tech Stack

- **Mod loader:** NeoForge 21.1.172
- **Minecraft:** 1.21.1
- **Java:** 21 (JDK 21, Temurin)
- **Build:** Gradle 8.8 with NeoForge Gradle plugin 7.0.145
- **Package:** `com.specterrealm.core`
- **Main class:** `src/main/java/com/specterrealm/core/SpecterRealmCore.java`

## Key Directories

```
src/main/
├── java/com/specterrealm/core/
│   └── SpecterRealmCore.java          # All item registration lives here (for now)
└── resources/
    ├── META-INF/neoforge.mods.toml    # Mod metadata, dependencies
    ├── assets/specterrealm/
    │   ├── lang/en_us.json            # All display names
    │   ├── models/item/               # One JSON per item (parent: minecraft:item/generated)
    │   └── textures/
    │       ├── gui/                   # book_texture.png, crafting_texture.png, filler_texture.png
    │       └── item/                  # One PNG per item
    └── data/specterrealm/
        ├── patchouli_books/field_manual/book.json  # Field Manual config
        ├── recipes/                   # Crafting recipes (JSON, added as parts are built)
        └── tags/item/                 # Item tags, including c: convention tags
```

## Build Commands

```bash
./gradlew build          # Compile + validate all JSON + produce JAR
./gradlew check          # Run validateResources (JSON validation) only
./gradlew runClient      # Launch Minecraft client for testing
./gradlew runServer      # Launch dedicated server for testing
```

JAR output: `build/libs/specterrealm-<version>.jar`

## Current Features

### Icon Items (14 total)
All registered via `DeferredRegister.Items`, `stacksTo(1)`, `fireResistant()`.

**Quest icons** (9): gathering, combat, exploration, crafting, tech, magic, boss, secret, complete
**Chapter icons** (5): survive, establish, automate, scale, command

All grouped under item tag `#specterrealm:quest_icon`.

### Field Manual (Patchouli Book)
- Book JSON at `data/specterrealm/patchouli_books/field_manual/book.json`
- Custom GUI textures: `book_texture.png`, `crafting_texture.png`, `filler_texture.png`
- Color scheme: dark green theme (`cover_color: 2C4A2E`)
- No chapters/entries defined yet — those go in subfolders of `patchouli_books/field_manual/`

## Planned: Crafting Parts System

The next major feature. Items representing industrial components (gears, plates, rods, wires, screws, etc.) across multiple materials, designed to gate tech-tree recipes without depending on a specific tech mod.

**Design principles:**
- Every item has a config toggle so pack devs can disable unused parts
- Items are tagged under `c:` convention tags (`c:gears/iron`, `c:plates/copper`, etc.) for cross-mod interop — this replaces the old Ore Dictionary
- Recipes use `#c:ingots/iron` style tag ingredients, not specific items, so any mod's material works as input
- Materials cover vanilla + common tech pack metals: iron, copper, gold, netherite, wood, stone, obsidian, diamond, emerald, and optionally tin/lead/silver (common across tech mods)
- For pack-specific mod materials (e.g. Create's brass), KubeJS/CraftTweaker in the pack handles equivalency — not this mod

**Part categories being built:**
- Mechanical: gears, rods/shafts, screws, bolts
- Sheet goods: thin plates, large plates
- Electrical: wires (fine/heavy gauge), processors/circuits
- Structural: frames, boxes/casings

## Conventions

### Adding a new item
1. Register in `SpecterRealmCore.java` via `ITEMS.register(...)`
2. Add model JSON to `assets/specterrealm/models/item/<name>.json`
3. Add texture PNG to `assets/specterrealm/textures/item/<name>.png`
4. Add lang entry to `assets/specterrealm/lang/en_us.json`
5. Add item tag entries under `data/specterrealm/tags/item/` and `data/c/tags/item/` as appropriate
6. Add recipe JSON to `data/specterrealm/recipes/<name>.json` if craftable

### Item model boilerplate
```json
{
  "parent": "minecraft:item/generated",
  "textures": { "layer0": "specterrealm:item/<name>" }
}
```

### Convention tags (c: namespace)
Use `data/c/tags/item/<category>/<material>.json` to tag items under the common namespace.
Example: `data/c/tags/item/gears/iron.json` → tags `specterrealm:iron_gear` so other mods' recipes accept it.

### Config system (planned)
NeoForge `ModConfigSpec` — `common.toml` config file with boolean toggles per asset or per material group. Class will live at `com.specterrealm.core.SpecterRealmConfig`.

## CI/CD

- `.github/workflows/build.yml` — builds on every PR
- `.github/workflows/release.yml` — builds and creates GitHub release on `v*` tags
- Java: Temurin 21, gradle caching enabled
