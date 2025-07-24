#!/bin/sh

# Exit immediately if a command exits with a non-zero status.
set -e
echo "--- Running ci_post_clone.sh ---"

echo "--- Copy Google Service Account ---"
echo -n "$GOOGLE_SERVICE_PLIST_BASE64" | base64 --decode -o $GOOGLE_SERVICE_PATH


# Define paths
ROOT_DIR=$CI_WORKSPACE_PATH
REPO_DIR=$CI_PRIMARY_REPOSITORY_PATH
JDK_DIR="${CI_DERIVED_DATA_PATH}/JDK"
GRADLE_CACHE_DIR="${CI_DERIVED_DATA_PATH}/.gradle"
KMP_SHARED_MODULE_PATH="${REPO_DIR}/shared" # Adjust if your shared module is elsewhere

JDK_VERSION="21.0.8" # Or whatever JDK version your KMP project requires (e.g., "20.0.1")
ARCH_TYPE=""

# Determine architecture
if [[ $(uname -m) == "arm64" ]]; then
    echo " - Detected M1 (arm64)"
    ARCH_TYPE="macos-aarch64"
else
    echo " - Detected Intel (x64)"
    ARCH_TYPE="macos-x64"
fi

# --- Install JDK if needed ---
install_jdk_if_needed() {
    echo "\\nInstall JDK if needed"
    DETECT_LOC="${JDK_DIR}/.${JDK_VERSION}.${ARCH_TYPE}"

    if [ -f "$DETECT_LOC" ]; then
        echo " - Found a valid JDK installation, skipping install"
        return 0
    fi

    echo " - No valid JDK installation found, installing..."
    TAR_NAME="jdk-${JDK_VERSION}_${ARCH_TYPE}_bin.tar.gz"
    JDK_DOWNLOAD_URL="https://download.oracle.com/java/21/archive/${TAR_NAME}"

    curl -L -o "$TAR_NAME" "$JDK_DOWNLOAD_URL"
    tar xzf "$TAR_NAME" -C "$ROOT_DIR"

    rm -rf "$JDK_DIR"
    mkdir -p "$JDK_DIR"
    mv "${ROOT_DIR}/jdk-${JDK_VERSION}.jdk/Contents/Home" "$JDK_DIR"

    rm -r "${ROOT_DIR}/jdk-${JDK_VERSION}.jdk" || true # Use || true to prevent error if dir doesn't exist
    rm "$TAR_NAME"

    touch "$DETECT_LOC"
    echo " - Set JAVA_HOME in Xcode Cloud to ${JDK_DIR}/Home"
    export JAVA_HOME="${JDK_DIR}/Home"
    return 0
}

# --- Recover Gradle caches (optional, but recommended for speed) ---
recover_cache_files() {
    echo "\\nRecover cache files"
    if [ ! -d "$GRADLE_CACHE_DIR" ]; then
        echo " - No valid caches found, skipping"
        return 0
    fi
    echo " - Copying gradle cache to ${KMP_SHARED_MODULE_PATH}/.gradle"
    rm -rf "${KMP_SHARED_MODULE_PATH}/.gradle"
    cp -r "$GRADLE_CACHE_DIR" "${KMP_SHARED_MODULE_PATH}"
    return 0
}

# --- Store Gradle caches (optional, but recommended for speed) ---
store_cache_files() {
    echo "\\nStore cache files"
    if [ ! -d "${KMP_SHARED_MODULE_PATH}/.gradle" ]; then
        echo " - No gradle cache found to store, skipping"
        return 0
    fi
    echo " - Copying gradle cache to ${GRADLE_CACHE_DIR}"
    rm -rf "$GRADLE_CACHE_DIR"
    cp -r "${KMP_SHARED_MODULE_PATH}/.gradle" "$GRADLE_CACHE_DIR"
    return 0
}

# Execute functions
# recover_cache_files
# install_jdk_if_needed

# --- Build Kotlin Multiplatform shared module ---
echo "\\nBuilding Kotlin Multiplatform shared module..."
# Navigate to the root of your Gradle project (where gradlew is)
cd "$REPO_DIR"

# This task builds the KMP framework for Xcode.

# Return to the Repo directory for Xcode to continue its build

echo "Java Home is set to ${JAVA_HOME}"
java -version

./gradlew podinstall

# store_cache_files # Store caches after build

echo "--- ci_post_clone.sh finished ---"
