#!/usr/bin/env bash
# Load CURSEFORGE_API_KEY for GitHub Actions or local CI.
#
# Priority:
#   1. AWS Secrets Manager personal/curseforge-api-key (after configure-aws-credentials OIDC)
#   2. CURSEFORGE_API_KEY env (legacy GitHub secret — deprecated)
#
# Usage (GHA):
#   configure-aws-credentials with secrets.AWS_CURSEFORGE_UPLOAD_ROLE_ARN
#   bash scripts/ci_load_curseforge_api_key.sh [--required]
#
# Usage (workstation):
#   export AWS_PROFILE=platform-bootstrap AWS_REGION=us-west-2
#   eval "$(bash scripts/ci_load_curseforge_api_key.sh --export)"
set -euo pipefail

REQUIRED=0
EXPORT=0
while [[ $# -gt 0 ]]; do
  case "$1" in
    --required) REQUIRED=1; shift ;;
    --export) EXPORT=1; shift ;;
    *) echo "unknown arg: $1" >&2; exit 2 ;;
  esac
done

SM_ID="${CURSEFORGE_SM_SECRET_ID:-personal/curseforge-api-key}"
KEY=""

if command -v aws >/dev/null 2>&1 && aws sts get-caller-identity >/dev/null 2>&1; then
  if aws secretsmanager describe-secret --secret-id "$SM_ID" >/dev/null 2>&1; then
    KEY="$(aws secretsmanager get-secret-value --secret-id "$SM_ID" --query SecretString --output text)"
  fi
fi

if [[ -z "$KEY" && -n "${CURSEFORGE_API_KEY:-}" ]]; then
  KEY="$CURSEFORGE_API_KEY"
fi

if [[ -z "$KEY" ]]; then
  if [[ "$REQUIRED" -eq 1 ]]; then
    echo "::error::CurseForge API key unavailable. Upload personal/curseforge-api-key to SM and apply platform-bootstrap Terraform, or set CURSEFORGE_API_KEY." >&2
    exit 1
  fi
  echo "CurseForge API key not configured — skipping upload"
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    echo "configured=false" >> "$GITHUB_OUTPUT"
  fi
  exit 0
fi

if [[ -n "${GITHUB_ENV:-}" ]]; then
  echo "::add-mask::$KEY"
  echo "CURSEFORGE_API_KEY=$KEY" >> "$GITHUB_ENV"
fi

if [[ "$EXPORT" -eq 1 ]]; then
  printf 'export CURSEFORGE_API_KEY=%q\n' "$KEY"
fi

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  echo "configured=true" >> "$GITHUB_OUTPUT"
fi
