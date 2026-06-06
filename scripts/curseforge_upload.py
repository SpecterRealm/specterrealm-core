#!/usr/bin/env python3
"""
Upload a file to a CurseForge Minecraft project via the legacy upload API.

Uses POST /api/projects/{projectId}/upload-file (multipart metadata + file).
Auth: CURSEFORGE_API_KEY env var → X-Api-Token header (CurseForge console key).

Usage:
  python3 scripts/curseforge_upload.py \\
    --project-id 1563209 \\
    --file dist/Colony-Protocol-Verdant-0.1.3-rc.11-curseforge.zip \\
    --version 0.1.3-rc.11 \\
    --changelog-file CHANGELOG.md \\
    --project-type modpack

  python3 scripts/curseforge_upload.py \\
    --project-id 1564254 \\
    --file build/libs/specterrealm-0.0.1.jar \\
    --version 0.0.1 \\
    --changelog-file CHANGELOG.md \\
    --project-type mod \\
    --loaders NeoForge

Dry run (print metadata only):
  python3 scripts/curseforge_upload.py ... --dry-run

Requires network and CURSEFORGE_API_KEY.
"""
from __future__ import annotations

import argparse
import json
import os
import re
import sys
import uuid
from pathlib import Path
from typing import Any
from urllib.error import HTTPError
from urllib.request import Request, urlopen

API_BASE = "https://minecraft.curseforge.com/api"
DEFAULT_MC = "1.21.1"
KNOWN_LOADER_IDS = {
    "neoforge": 10150,
    "forge": 7498,
    "fabric": 7499,
    "quilt": 9153,
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Upload a file to CurseForge")
    parser.add_argument("--project-id", type=int, required=True)
    parser.add_argument("--file", type=Path, required=True, help="JAR or modpack zip")
    parser.add_argument("--version", required=True, help="Release version (for changelog + display name)")
    parser.add_argument(
        "--changelog-file",
        type=Path,
        default=Path("CHANGELOG.md"),
        help="Changelog markdown file (extracts ## [version] section)",
    )
    parser.add_argument(
        "--changelog",
        default="",
        help="Inline changelog (overrides --changelog-file when set)",
    )
    parser.add_argument(
        "--project-type",
        choices=("mod", "modpack"),
        default="modpack",
        help="mod adds loader game version IDs; modpack uses Minecraft version only",
    )
    parser.add_argument(
        "--minecraft-version",
        default=DEFAULT_MC,
        help=f"Minecraft version string (default: {DEFAULT_MC})",
    )
    parser.add_argument(
        "--loaders",
        nargs="*",
        default=["NeoForge"],
        help="Mod loader names for --project-type mod (default: NeoForge)",
    )
    parser.add_argument(
        "--release-type",
        choices=("alpha", "beta", "release"),
        default="",
        help="Override inferred release channel from version string",
    )
    parser.add_argument("--display-name", default="", help="CF display name (default: derived from file stem)")
    parser.add_argument(
        "--manual-release",
        action="store_true",
        help="Set isMarkedForManualRelease (wait for manual publish after approval)",
    )
    parser.add_argument("--dry-run", action="store_true")
    return parser.parse_args()


def api_token() -> str:
    token = os.environ.get("CURSEFORGE_API_KEY", "").strip()
    if not token:
        raise SystemExit("CURSEFORGE_API_KEY is not set")
    return token


def infer_release_type(version: str) -> str:
    lower = version.lower()
    if "-dev." in lower or lower.endswith("-dev"):
        return "alpha"
    if "-" in lower:
        return "beta"
    return "release"


def extract_changelog(changelog_file: Path, version: str) -> str:
    if not changelog_file.is_file():
        return f"Release {version}"
    text = changelog_file.read_text(encoding="utf-8")
    escaped = re.escape(version)
    pattern = rf"^## \[{escaped}\]\s*[^\n]*\n(.*?)(?=^## \[|\Z)"
    match = re.search(pattern, text, flags=re.MULTILINE | re.DOTALL)
    if not match:
        return f"Release {version}"
    body = match.group(1).strip()
    return body or f"Release {version}"


def fetch_game_versions(token: str) -> list[dict[str, Any]]:
    req = Request(
        f"{API_BASE}/game/versions",
        headers={"X-Api-Token": token, "Accept": "application/json"},
    )
    with urlopen(req, timeout=60) as resp:
        data = json.load(resp)
    if not isinstance(data, list):
        raise SystemExit(f"Unexpected game versions response: {type(data)!r}")
    return data


def resolve_game_version_ids(
    token: str,
    minecraft_version: str,
    loaders: list[str],
    *,
    project_type: str,
) -> list[int]:
    versions = fetch_game_versions(token)
    by_name = {str(v.get("name", "")): int(v["id"]) for v in versions if "id" in v}

    mc_id = by_name.get(minecraft_version)
    if mc_id is None:
        raise SystemExit(
            f"Could not resolve CurseForge game version id for Minecraft {minecraft_version!r}. "
            "Check --minecraft-version or CURSEFORGE_API_KEY permissions."
        )

    ids = [mc_id]
    if project_type == "mod":
        for loader in loaders:
            known = KNOWN_LOADER_IDS.get(loader.lower())
            if known is not None:
                ids.append(known)
                continue
            loader_id = by_name.get(loader)
            if loader_id is None:
                raise SystemExit(
                    f"Could not resolve loader {loader!r}. "
                    f"Known loaders: {', '.join(sorted(KNOWN_LOADER_IDS))}"
                )
            ids.append(loader_id)
    return ids


def build_metadata(args: argparse.Namespace, changelog: str, game_version_ids: list[int]) -> dict[str, Any]:
    release_type = args.release_type or infer_release_type(args.version)
    display_name = args.display_name.strip()
    if not display_name:
        display_name = args.file.stem

    metadata: dict[str, Any] = {
        "changelog": changelog,
        "changelogType": "markdown",
        "displayName": display_name,
        "gameVersions": game_version_ids,
        "releaseType": release_type,
        "isMarkedForManualRelease": bool(args.manual_release),
    }
    return metadata


def encode_multipart(metadata: dict[str, Any], file_path: Path) -> tuple[bytes, str]:
    boundary = f"----curseforge-upload-{uuid.uuid4().hex}"
    file_bytes = file_path.read_bytes()
    metadata_json = json.dumps(metadata, ensure_ascii=False)

    parts: list[bytes] = []
    for name, value in (("metadata", metadata_json),):
        parts.extend(
            [
                f"--{boundary}\r\n".encode(),
                f'Content-Disposition: form-data; name="{name}"\r\n\r\n'.encode(),
                value.encode("utf-8"),
                b"\r\n",
            ]
        )

    filename = file_path.name
    parts.extend(
        [
            f"--{boundary}\r\n".encode(),
            (
                f'Content-Disposition: form-data; name="file"; filename="{filename}"\r\n'
                f"Content-Type: application/octet-stream\r\n\r\n"
            ).encode(),
            file_bytes,
            b"\r\n",
            f"--{boundary}--\r\n".encode(),
        ]
    )
    body = b"".join(parts)
    content_type = f"multipart/form-data; boundary={boundary}"
    return body, content_type


def upload_file(project_id: int, file_path: Path, metadata: dict[str, Any], token: str) -> int:
    body, content_type = encode_multipart(metadata, file_path)
    url = f"{API_BASE}/projects/{project_id}/upload-file"
    req = Request(
        url,
        data=body,
        method="POST",
        headers={
            "X-Api-Token": token,
            "Content-Type": content_type,
            "Accept": "application/json",
        },
    )
    try:
        with urlopen(req, timeout=600) as resp:
            payload = json.load(resp)
    except HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="replace")
        raise SystemExit(f"CurseForge upload failed ({exc.code}): {detail}") from exc

    file_id = payload.get("id")
    if file_id is None:
        raise SystemExit(f"CurseForge upload response missing id: {payload!r}")
    return int(file_id)


def main() -> int:
    args = parse_args()
    file_path = args.file.resolve()
    if not file_path.is_file():
        raise SystemExit(f"File not found: {file_path}")

    changelog = args.changelog.strip() or extract_changelog(args.changelog_file, args.version)
    token = api_token()
    game_version_ids = resolve_game_version_ids(
        token,
        args.minecraft_version,
        args.loaders,
        project_type=args.project_type,
    )
    metadata = build_metadata(args, changelog, game_version_ids)

    print(f"Project: {args.project_id}")
    print(f"File: {file_path.name} ({file_path.stat().st_size} bytes)")
    print(f"Release type: {metadata['releaseType']}")
    print(f"Game version IDs: {game_version_ids}")

    if args.dry_run:
        print("Dry run — metadata:")
        print(json.dumps(metadata, indent=2, ensure_ascii=False))
        return 0

    file_id = upload_file(args.project_id, file_path, metadata, token)
    print(f"Uploaded CurseForge file id: {file_id}")
    print(f"::set-output name=file_id::{file_id}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
