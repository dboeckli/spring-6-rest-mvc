#!/bin/bash

# Safer Bash scripting options
# https://web.archive.org/web/20190329060125/https://vaneyckt.io/posts/safer_bash_scripts_with_set_euxo_pipefail/
set -o pipefail # Don't hide errors within pipes
set -o nounset  # Abort on unbound variable
set -o errexit  # Abort on nonzero exit status

# Function to log errors and exit
function error_exit {
	echo "Error: $1" >&2
	exit 1
}

CHART_VERSION="${1:-}"
OWNER="${CLOUDSMITH_OWNER:-dboeckli}"
REPO="${CLOUDSMITH_REPOSITORY:-dboeckli-cloudsmith-repo}"

if [ -z "$CHART_VERSION" ]; then
	error_exit "Usage: $0 <chart-version>"
fi
command -v curl >/dev/null 2>&1 || error_exit "curl is not installed or not in PATH."
command -v jq >/dev/null 2>&1 || error_exit "jq is not installed or not in PATH."
[ -n "${CLOUDSMITH_API_KEY:-}" ] || error_exit "CLOUDSMITH_API_KEY is not set."

echo "Tagging Cloudsmith package for chart version: $CHART_VERSION"

PACKAGES=$(curl -sS -H "X-Api-Key: $CLOUDSMITH_API_KEY" \
	"https://api.cloudsmith.io/v1/packages/$OWNER/$REPO/?page_size=100")

IDENTIFIER=$(echo "$PACKAGES" | jq -r --arg v "$CHART_VERSION" \
	'.[] | select(((.tags.version // []) | index($v)) != null) | .slug_perm' | head -1)

if [ -z "$IDENTIFIER" ]; then
	error_exit "No Cloudsmith package found for version tag $CHART_VERSION"
fi
echo "Found package identifier: $IDENTIFIER"

RESULT=$(curl -sS -X POST -H "X-Api-Key: $CLOUDSMITH_API_KEY" -H "Content-Type: application/json" \
	-d '{"tags": ["helm"]}' \
	"https://api.cloudsmith.io/v1/packages/$OWNER/$REPO/$IDENTIFIER/tag/")
echo "$RESULT" | jq -r '"Tag status: " + .status_str'
echo "Cloudsmith package $IDENTIFIER tagged as helm (version $CHART_VERSION)"
