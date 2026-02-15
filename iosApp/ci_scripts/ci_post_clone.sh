#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e
echo "--- Running ci_post_clone.sh ---"

echo "--- Copy Google Service Account ---"
echo -n "$GOOGLE_SERVICE_PLIST_BASE64" | base64 --decode -o "$GOOGLE_SERVICE_PATH"


# Define paths
REPO_DIR=$CI_PRIMARY_REPOSITORY_PATH

WCCRM_HYMNS_JSON="wccrm_hymns_original.json"
WCCRM_SHEET_MUSIC_ARCHIVE="wccrm_hymns_v2.zip"
WCCRM_TUNES_ARCHIVE="tunes_wccrm_midi_archive_hymns.zip"

COMPOSE_RESOURCES_DIR="shared/src/commonMain/composeResources/files"

cd "$REPO_DIR"

# ... (Existing file cleanup and directory creation logic remains the same) ...
rm -rf "$COMPOSE_RESOURCES_DIR"/json/*.json
rm -rf "$COMPOSE_RESOURCES_DIR"/openlyrics/*.zip
rm -rf "$COMPOSE_RESOURCES_DIR"/sheets/*.zip
rm -rf "$COMPOSE_RESOURCES_DIR"/tunes/*.zip
rm -rf "$COMPOSE_RESOURCES_DIR"/manifest/*.json
mkdir -p "$COMPOSE_RESOURCES_DIR"/sheets
mkdir -p "$COMPOSE_RESOURCES_DIR"/manifest

echo "$WCCRM_HYMNS_MANIFEST_JSON" > "$COMPOSE_RESOURCES_DIR"/manifest/filesmanifest.json

echo "Downloading song assets..."
curl --output "$COMPOSE_RESOURCES_DIR"/json/"$WCCRM_HYMNS_JSON" "$WCCRM_HYMNS_JSON_URL"
curl --output "$COMPOSE_RESOURCES_DIR"/tunes/"$WCCRM_TUNES_ARCHIVE" "$WCCRM_TUNES_ASSET_DOWNLOAD_URL"
curl --output "$COMPOSE_RESOURCES_DIR"/sheets/"$WCCRM_SHEET_MUSIC_ARCHIVE" "$WCCRM_SHEET_MUSIC_ASSET_DOWNLOAD_URL"
echo "Download assets complete"

# --- CHANGED SECTION START ---
echo "Installing Java 21 via Homebrew..."

# Unlink old java versions if necessary to avoid conflicts (optional but recommended)
# brew unlink java || true

# Install OpenJDK 21
brew install openjdk@21

export JAVA_HOME=$(brew --prefix openjdk@21)

# Add Java to the PATH
export PATH="$JAVA_HOME/bin:$PATH"

echo "Java version:"
java -version
# --- CHANGED SECTION END ---

./gradlew :shared:compileKotlinIosArm64

# store_cache_files # Store caches after build

echo "--- ci_post_clone.sh finished ---"
