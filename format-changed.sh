#!/bin/bash
#
# Runs Liferay Source Formatter only on locally changed Java files.
#
# Usage:
# ./format-changed.sh            # Format uncommitted changes (staged + unstaged)
# ./format-changed.sh --staged   # Format only staged files
# ./format-changed.sh --branch   # Format all files changed on current branch vs master
#

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

MODE="${1:---all}"

case "$MODE" in
	--staged)
		FILES=$(git -C "$PROJECT_DIR" diff --cached --name-only --diff-filter=ACMR -- '*.java')
		;;
	--branch)
		BASE_BRANCH="${2:-master}"
		FILES=$(git -C "$PROJECT_DIR" diff --name-only --diff-filter=ACMR "$BASE_BRANCH"...HEAD -- '*.java')
		;;
	--all|*)
		FILES=$( (git -C "$PROJECT_DIR" diff --name-only --diff-filter=ACMR -- '*.java'; \
				git -C "$PROJECT_DIR" diff --cached --name-only --diff-filter=ACMR -- '*.java'; \
				git -C "$PROJECT_DIR" ls-files --others --exclude-standard -- '*.java') | sort -u)
		;;
esac

if [ -z "$FILES" ]; then
	echo "No changed Java files found. Nothing to format."
	exit 0
fi

FILE_COUNT=$(echo "$FILES" | wc -l)
echo "Formatting ${FILE_COUNT} changed Java file(s):"
echo "$FILES" | sed 's/^/  /'
echo ""

# Build comma-separated file list with ./ prefix for source formatter
SOURCE_FILES=$(echo "$FILES" | sed 's|^|./|' | paste -sd ',' -)

# Run the formatter using formatChanged profile which passes -Dsource.files
mvn exec:java -PformatChanged \
	-Dsource.changed.files="${SOURCE_FILES}" \
	-pl . \
	-N

echo ""
echo "Done. Formatted ${FILE_COUNT} file(s)."
