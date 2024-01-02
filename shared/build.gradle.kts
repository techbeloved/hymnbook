import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.native.cocoapods)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    explicitApi()

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(libs.kotlinx.collections.immutable)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.io)
                implementation(libs.kotlinx.datetime)

                // Voyager
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.bottomSheetNavigator)

                // Sqldelight
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.adapters)

                // okio
                implementation(libs.squareup.okio)

                // xmlutil
                implementation(libs.xmlutil.core)
                implementation(libs.xmlutil.serialization)
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.compose.activity)
                implementation(libs.sqldelight.android)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.sqldelight.jvm)
            }
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.sqldelight.jvm)
            }
        }
    }

    cocoapods {
        version = "1.0"
        summary = "Hymnbook multiplatform"
        homepage = "none.for.now"
        name = "hymnbook-shared"
        ios.deploymentTarget = "13.5" // minSdk

        framework {
            baseName = "shared"
            embedBitcode(BitcodeEmbeddingMode.BITCODE)
        }

        pod("UnzipKit") {
            version = "1.9"
        }
    }
}

android {
    namespace = "com.techbeloved.hymnbook.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }

    kotlin {
        jvmToolchain(17)
    }

    sourceSets["main"].apply {
        res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.techbeloved.hymnbook")
        }
    }
}