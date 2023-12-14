plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
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
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.techbeloved.hymnbook")
        }
    }
}