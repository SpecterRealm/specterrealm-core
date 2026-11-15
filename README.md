# specterrealm-core

Shared NeoForge library mod for all Colony Protocol packs (`modId: specterrealm`).

CurseForge: [SpecterRealm Core](https://www.curseforge.com/minecraft/mc-mods/specterrealm-core) (project **1564254**).

## Build

```bash
gradle build
# JAR → build/libs/specterrealm-{version}.jar
```

Release version from tag: `gradle build -Pmod_version=0.0.2`

## Release + CurseForge upload

1. Tag `v*` on `main` → GitHub Release + optional CurseForge upload
2. API key: source of truth is homelab Vault, KV `homelab/curseforge/api-key`, field `token`. This repo is public, so its jobs run on GitHub-hosted runners (a homelab runner on a public repo would run fork PR code on the LAN) and cannot reach Vault; CI reads the `CURSEFORGE_API_KEY` Actions secret, which platform-bootstrap writes from that Vault path. `scripts/ci_load_curseforge_api_key.sh` masks it. No cloud role or `id-token` permission. Variable `CURSEFORGE_PROJECT_ID` = `1564254`.
   Workstation: `vault login`, then `eval "$(bash scripts/ci_load_curseforge_api_key.sh --export)"`.

Manual upload without tag: **Actions → CurseForge Upload → Run workflow**

Dry run: same form with `dry_run` ticked (any branch). It loads the key, builds the JAR and runs `curseforge_upload.py --dry-run`, which calls the CF game-version API with the key and prints the metadata without posting. Pass: log shows `CurseForge API key loaded from CURSEFORGE_API_KEY environment` and `Dry run — metadata:`, and no `Uploaded CurseForge file id`.

Upload script: `scripts/curseforge_upload.py` (same as Verdant pack repo).

After a new mod file ships, update the Verdant pack pin:

```bash
# in minecraft-modpack-cp-verdant
python3 scripts/update_specterrealm_pin.py --file-id <new_cf_file_id>
```

See Verdant `docs/curseforge-upload.md` for the full automation map.
