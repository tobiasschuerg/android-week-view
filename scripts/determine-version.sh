#!/usr/bin/env bash
# determine-version.sh — Single-pass conventional-commit version bump + release notes.
#
# Outputs (via $GITHUB_OUTPUT when in CI, stdout otherwise):
#   skip      — "true" if no release is needed
#   version   — next semver version (e.g. 2.3.0)
#   previous  — latest existing semver tag
#   notes     — markdown release notes
#
# Environment:
#   REPO_URL  — (optional) base URL for changelog links; falls back to git remote

set -euo pipefail

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
output() {
  local key="$1" value="$2"
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    echo "${key}=${value}" >> "$GITHUB_OUTPUT"
  else
    echo "${key}=${value}"
  fi
}

output_multiline() {
  local key="$1" value="$2"
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    {
      echo "${key}<<NOTES_EOF"
      echo "$value"
      echo "NOTES_EOF"
    } >> "$GITHUB_OUTPUT"
  else
    echo "${key}<<NOTES_EOF"
    echo "$value"
    echo "NOTES_EOF"
  fi
}

# ---------------------------------------------------------------------------
# Find latest semver tag (bare X.Y.Z — no v-prefix, no pre-release)
# ---------------------------------------------------------------------------
LATEST_TAG=$(git tag -l --sort=-v:refname | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | head -1 || true)

if [[ -z "$LATEST_TAG" ]]; then
  echo "No semver tags found, starting from 0.0.0"
  LATEST_TAG="0.0.0"
  COMMIT_RANGE="HEAD"
else
  echo "Latest tag: $LATEST_TAG"
  COMMIT_RANGE="${LATEST_TAG}..HEAD"
fi

# ---------------------------------------------------------------------------
# Check for new commits
# ---------------------------------------------------------------------------
COMMIT_COUNT=$(git rev-list --count $COMMIT_RANGE 2>/dev/null || echo "0")

if [[ "$COMMIT_COUNT" == "0" ]]; then
  echo "No new commits since $LATEST_TAG — skipping release"
  output "skip" "true"
  exit 0
fi

# ---------------------------------------------------------------------------
# Repo URL for changelog links
# ---------------------------------------------------------------------------
REPO_URL="${REPO_URL:-$(git remote get-url origin 2>/dev/null || echo "")}"
# Normalise SSH → HTTPS
REPO_URL="${REPO_URL%.git}"
REPO_URL="${REPO_URL/git@github.com:/https://github.com/}"

# ---------------------------------------------------------------------------
# Single pass: determine bump type AND build categorised notes
# ---------------------------------------------------------------------------
BUMP="none"
BREAKING=""
FEATURES=""
FIXES=""
OTHER=""

while IFS= read -r COMMIT_HASH; do
  SUBJECT=$(git log -1 --format='%s' "$COMMIT_HASH")
  BODY=$(git log -1 --format='%b' "$COMMIT_HASH")
  SHORT="${COMMIT_HASH:0:7}"
  LINE="- ${SUBJECT} (${SHORT})"

  IS_BREAKING=false
  if echo "$SUBJECT" | grep -qE '^[a-z]+(\(.+\))?!:'; then
    IS_BREAKING=true
  elif echo "$BODY" | grep -q '^BREAKING CHANGE:'; then
    IS_BREAKING=true
  fi

  # Categorise for notes
  if [[ "$IS_BREAKING" == "true" ]]; then
    BREAKING="${BREAKING}${LINE}"$'\n'
  elif echo "$SUBJECT" | grep -qE '^feat(\(.+\))?:'; then
    FEATURES="${FEATURES}${LINE}"$'\n'
  elif echo "$SUBJECT" | grep -qE '^fix(\(.+\))?:'; then
    FIXES="${FIXES}${LINE}"$'\n'
  else
    OTHER="${OTHER}${LINE}"$'\n'
  fi

  # Determine bump (highest wins: major > minor > patch)
  if [[ "$IS_BREAKING" == "true" ]]; then
    BUMP="major"
  elif echo "$SUBJECT" | grep -qE '^feat(\(.+\))?:'; then
    [[ "$BUMP" != "major" ]] && BUMP="minor"
  elif echo "$SUBJECT" | grep -qE '^fix(\(.+\))?:'; then
    [[ "$BUMP" == "none" ]] && BUMP="patch"
  fi
done < <(git rev-list $COMMIT_RANGE)

# ---------------------------------------------------------------------------
# Skip if no releasable commits
# ---------------------------------------------------------------------------
if [[ "$BUMP" == "none" ]]; then
  echo "No feat/fix commits found — skipping release"
  output "skip" "true"
  exit 0
fi

# ---------------------------------------------------------------------------
# Calculate next version
# ---------------------------------------------------------------------------
IFS='.' read -r MAJOR MINOR PATCH <<< "$LATEST_TAG"
case "$BUMP" in
  major) MAJOR=$((MAJOR + 1)); MINOR=0; PATCH=0 ;;
  minor) MINOR=$((MINOR + 1)); PATCH=0 ;;
  patch) PATCH=$((PATCH + 1)) ;;
esac
NEXT_VERSION="${MAJOR}.${MINOR}.${PATCH}"

echo "Bump type: $BUMP"
echo "Next version: $NEXT_VERSION"

# ---------------------------------------------------------------------------
# Build release notes
# ---------------------------------------------------------------------------
NOTES=""
if [[ -n "$BREAKING" ]]; then
  NOTES+="## Breaking Changes"$'\n'"${BREAKING}"$'\n'
fi
if [[ -n "$FEATURES" ]]; then
  NOTES+="## Features"$'\n'"${FEATURES}"$'\n'
fi
if [[ -n "$FIXES" ]]; then
  NOTES+="## Bug Fixes"$'\n'"${FIXES}"$'\n'
fi
if [[ -n "$OTHER" ]]; then
  NOTES+="## Other Changes"$'\n'"${OTHER}"$'\n'
fi
if [[ -n "$REPO_URL" ]]; then
  NOTES+="**Full Changelog**: [${LATEST_TAG}...${NEXT_VERSION}](${REPO_URL}/compare/${LATEST_TAG}...${NEXT_VERSION})"
fi

# ---------------------------------------------------------------------------
# Outputs
# ---------------------------------------------------------------------------
output "skip" "false"
output "version" "$NEXT_VERSION"
output "previous" "$LATEST_TAG"
output_multiline "notes" "$NOTES"
