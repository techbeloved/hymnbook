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
IOS_COMPOSE_RESOURCES_DIR="shared/src/iosMain/composeResources/files"

cd "$REPO_DIR"

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

curl -s "https://get.sdkman.io?ci=true&rcupdate=false" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.8-amzn

export JAVA_HOME=/Users/local/.sdkman/candidates/java/current

./gradlew :shared:compileKotlinIosArm64

# store_cache_files # Store caches after build

echo "--- ci_post_clone.sh finished ---"
