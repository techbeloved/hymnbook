#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e
echo "--- Running ci_post_clone.sh ---"

echo "--- Copy Google Service Account ---"
echo -n "$GOOGLE_SERVICE_PLIST_BASE64" | base64 --decode -o $GOOGLE_SERVICE_PATH


# Define paths
REPO_DIR=$CI_PRIMARY_REPOSITORY_PATH

curl -s "https://get.sdkman.io?ci=true&rcupdate=false" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.8-amzn

export JAVA_HOME=/Users/local/.sdkman/candidates/java/current

cd "$REPO_DIR"

./gradlew :shared:compileKotlinIosArm64

# store_cache_files # Store caches after build

echo "--- ci_post_clone.sh finished ---"
