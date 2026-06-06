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
2. API key in AWS SM `personal/curseforge-api-key`; CI reads via OIDC (`AWS_CURSEFORGE_UPLOAD_ROLE_ARN` from platform-bootstrap Terraform). Variable `CURSEFORGE_PROJECT_ID` = `1564254`.

Manual upload without tag: **Actions → CurseForge Upload → Run workflow**

Upload script: `scripts/curseforge_upload.py` (same as Verdant pack repo).

After a new mod file ships, update the Verdant pack pin:

```bash
# in minecraft-modpack-cp-verdant
python3 scripts/update_specterrealm_pin.py --file-id <new_cf_file_id>
```

See Verdant `docs/curseforge-upload.md` for the full automation map.
