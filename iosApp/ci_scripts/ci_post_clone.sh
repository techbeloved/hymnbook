#!/bin/sh

# Exit immediately if a command exits with a non-zero status.
set -e
echo "--- Running ci_post_clone.sh ---"

echo "--- Copy Google Service Account ---"
echo -n "$GOOGLE_SERVICE_PLIST_BASE64" | base64 --decode -o $GOOGLE_SERVICE_PATH


# Define paths
REPO_DIR=$CI_PRIMARY_REPOSITORY_PATH

brew install openjdk@21

export JAVA_HOME=/usr/local/opt/openjdk@21

cd "$REPO_DIR"

./gradlew podinstall

# store_cache_files # Store caches after build

echo "--- ci_post_clone.sh finished ---"
