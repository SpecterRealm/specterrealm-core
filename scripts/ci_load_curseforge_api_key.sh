#!/usr/bin/env bash
# Load CURSEFORGE_API_KEY for GitHub Actions or a workstation.
#
# Source of truth: homelab Vault, KV v2 mount `homelab`, path `curseforge/api-key`, field `token`
# (CLI: `vault kv get -field=token homelab/curseforge/api-key`). NEXUS-1782 / CP-372 moved it here
# from Secrets Manager in the old standalone cloud account, which is being closed.
#
# Order:
#   1. Vault, when VAULT_TOKEN is set, or VAULT_APPROLE_ROLE_ID + VAULT_APPROLE_SECRET_ID are set
#      (AppRole login, same request as platform-bootstrap scripts/vault-approle-login.sh).
#   2. CURSEFORGE_API_KEY already in the environment (local export, or an Actions secret on a
#      GitHub-hosted runner that cannot reach Vault).
#
# If Vault credentials are given and the login or read fails, the script fails (it never falls
# back silently). With no credentials and no env key: exit 1 with --required, else
# configured=false and exit 0 (Release skips the upload).
#
# The key and the Vault token are never printed. In Actions the key is masked before it is
# written to GITHUB_ENV. Only --export prints the key, for `eval` on a workstation.
#
# Usage (GHA, homelab runner):
#   env VAULT_APPROLE_ROLE_ID / VAULT_APPROLE_SECRET_ID from repo secrets
#   bash scripts/ci_load_curseforge_api_key.sh [--required]
# Usage (workstation, signed in to Vault):
#   export VAULT_ADDR=https://vault.specterrealm.com VAULT_TOKEN=...   # or `vault login`
#   eval "$(bash scripts/ci_load_curseforge_api_key.sh --export)"
set -euo pipefail
set +x

REQUIRED=0
EXPORT=0
while [[ $# -gt 0 ]]; do
  case "$1" in
    --required) REQUIRED=1; shift ;;
    --export) EXPORT=1; shift ;;
    *) echo "unknown arg: $1" >&2; exit 2 ;;
  esac
done

VAULT_ADDR="${VAULT_ADDR:-${HOMELAB_VAULT_ADDR:-https://vault.specterrealm.com}}"
VAULT_KV_MOUNT="${CURSEFORGE_VAULT_MOUNT:-homelab}"
VAULT_KV_PATH="${CURSEFORGE_VAULT_PATH:-curseforge/api-key}"
VAULT_KV_FIELD="${CURSEFORGE_VAULT_FIELD:-token}"

# Workstation: pick up the vault CLI token file if no env token.
if [[ -z "${VAULT_TOKEN:-}" && -z "${VAULT_APPROLE_ROLE_ID:-}" && -r "${HOME}/.vault-token" && -z "${GITHUB_ACTIONS:-}" ]]; then
  VAULT_TOKEN="$(cat "${HOME}/.vault-token")"
fi

_mask() {
  [[ -n "${GITHUB_ACTIONS:-}" && -n "$1" ]] && echo "::add-mask::$1"
  return 0
}

_need() {
  command -v "$1" >/dev/null 2>&1 || { echo "::error::$1 not found on this runner" >&2; exit 1; }
}

_vault_login() {
  local body resp code
  body="$(jq -nc --arg r "${VAULT_APPROLE_ROLE_ID}" --arg s "${VAULT_APPROLE_SECRET_ID}" \
    '{role_id: $r, secret_id: $s}')"
  resp="$(curl -sS -m 20 -w '\n%{http_code}' -X POST -H 'Content-Type: application/json' \
    --data-binary @- "${VAULT_ADDR%/}/v1/auth/approle/login" <<<"${body}")" || {
    echo "::error::Vault AppRole login: could not reach ${VAULT_ADDR}" >&2
    return 1
  }
  code="${resp##*$'\n'}"
  resp="${resp%$'\n'*}"
  if [[ "${code}" != "200" ]]; then
    # Error bodies hold no token; print the code only to be safe.
    echo "::error::Vault AppRole login at ${VAULT_ADDR} returned HTTP ${code}" >&2
    return 1
  fi
  jq -r '.auth.client_token // empty' <<<"${resp}"
}

_vault_read() {
  local resp code
  resp="$(curl -sS -m 20 -w '\n%{http_code}' -H @<(printf 'X-Vault-Token: %s\n' "${VAULT_TOKEN}") \
    "${VAULT_ADDR%/}/v1/${VAULT_KV_MOUNT}/data/${VAULT_KV_PATH}")" || {
    echo "::error::Vault read: could not reach ${VAULT_ADDR}" >&2
    return 1
  }
  code="${resp##*$'\n'}"
  resp="${resp%$'\n'*}"
  if [[ "${code}" != "200" ]]; then
    echo "::error::Vault read ${VAULT_KV_MOUNT}/${VAULT_KV_PATH} returned HTTP ${code} (403: AppRole policy lacks read; 404: path not written yet)" >&2
    return 1
  fi
  jq -r --arg f "${VAULT_KV_FIELD}" '.data.data[$f] // empty' <<<"${resp}"
}

KEY=""
SOURCE=""

if [[ -n "${VAULT_TOKEN:-}" || ( -n "${VAULT_APPROLE_ROLE_ID:-}" && -n "${VAULT_APPROLE_SECRET_ID:-}" ) ]]; then
  _need curl
  _need jq
  if [[ -z "${VAULT_TOKEN:-}" ]]; then
    VAULT_TOKEN="$(_vault_login)" || exit 1
    if [[ -z "${VAULT_TOKEN}" ]]; then
      echo "::error::Vault AppRole login returned no client_token" >&2
      exit 1
    fi
  fi
  _mask "${VAULT_TOKEN}"
  KEY="$(_vault_read)" || exit 1
  if [[ -z "${KEY}" ]]; then
    echo "::error::Vault ${VAULT_KV_MOUNT}/${VAULT_KV_PATH} has no field '${VAULT_KV_FIELD}'" >&2
    exit 1
  fi
  SOURCE="Vault ${VAULT_KV_MOUNT}/${VAULT_KV_PATH} (field ${VAULT_KV_FIELD})"
elif [[ -n "${CURSEFORGE_API_KEY:-}" ]]; then
  KEY="${CURSEFORGE_API_KEY}"
  SOURCE="CURSEFORGE_API_KEY environment"
fi

if [[ -z "${KEY}" ]]; then
  if [[ "${REQUIRED}" -eq 1 ]]; then
    echo "::error::CurseForge API key unavailable: set VAULT_APPROLE_ROLE_ID/VAULT_APPROLE_SECRET_ID (or VAULT_TOKEN) to read ${VAULT_KV_MOUNT}/${VAULT_KV_PATH}, or set CURSEFORGE_API_KEY." >&2
    exit 1
  fi
  echo "CurseForge API key not configured — skipping upload"
  [[ -n "${GITHUB_OUTPUT:-}" ]] && echo "configured=false" >> "${GITHUB_OUTPUT}"
  exit 0
fi

_mask "${KEY}"
if [[ -n "${GITHUB_ENV:-}" ]]; then
  echo "CURSEFORGE_API_KEY=${KEY}" >> "${GITHUB_ENV}"
fi
if [[ "${EXPORT}" -eq 1 ]]; then
  printf 'export CURSEFORGE_API_KEY=%q\n' "${KEY}"
fi
echo "CurseForge API key loaded from ${SOURCE}" >&2
[[ -n "${GITHUB_OUTPUT:-}" ]] && echo "configured=true" >> "${GITHUB_OUTPUT}"
exit 0
