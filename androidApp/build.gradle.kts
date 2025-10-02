import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.triplet.play)
}

val isXcodeCloudBuild = System.getenv("CI_XCODE_CLOUD") != null
val keystorePropertiesFile = rootProject.file("keystore.properties")

val keystoreProperties = Properties()
if (!isXcodeCloudBuild) {
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    } else {
        val envVars = System.getenv()
        keystoreProperties.setProperty("storeFile", envVars["KEYSTORE"] ?: "")
        keystoreProperties.setProperty("keyAlias", envVars["ALIAS"] ?: "")
        keystoreProperties.setProperty("keyPassword", envVars["KEY_PASSWORD"] ?: "")
        keystoreProperties.setProperty("storePassword", envVars["KEY_STORE_PASSWORD"] ?: "")
    }
}

android {
    namespace = "com.techbeloved.hymnbook"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    if (!isXcodeCloudBuild) {
        signingConfigs {
            create("release_config") {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }


    defaultConfig {
        applicationId = "com.techbeloved.hymnbook"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 300
        versionName = "3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (!isXcodeCloudBuild) {
        buildTypes {
            release {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.getByName("release_config")
            }
            debug {
                applicationIdSuffix = ".debug"
                signingConfig = signingConfigs.getByName("release_config")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    playConfigs {
        // only enable for release build in CI (release should be built only on CI)
        register("release") {
            enabled.set(!isXcodeCloudBuild)
        }
    }
}

play {
    track.set("internal")
    userFraction.set(0.5)
    updatePriority.set(2)
    defaultToAppBundles.set(true)

    // Only enable in CI
    enabled.set(false)
}

dependencies {

    implementation(project(":shared"))
    implementation(project(":modules:media"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.app.update.ktx)
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.androidx.junit)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.test.ui.test.junit)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.test.ui.tooling.manifest)
}
