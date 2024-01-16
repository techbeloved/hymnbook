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
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    applyDefaultHierarchyTemplate()

    explicitApi()

    cocoapods {
        version = "1.0"
        summary = "Hymnbook multiplatform"
        homepage = "none.for.now"
        license = "Apache"
        ios.deploymentTarget = "15.5" // minSdk
        podfile = project.file("../iosApp/Podfile")
        framework {
            isStatic = false
            baseName = "shared"
            embedBitcode(BitcodeEmbeddingMode.BITCODE)
        }

        pod("SSZipArchive") {
            version = "2.5.5"
        }
    }

    sourceSets {
        commonMain.dependencies {
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
            implementation(libs.coroutines)

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

        androidMain.dependencies {
            api(libs.compose.activity)
            implementation(libs.sqldelight.android)
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.sqldelight.jvm)
                implementation(libs.coroutines.swing)
            }
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }

        // Share code between android and desktop targets
        val androidAndDesktop by creating {
            dependsOn(commonMain.get())
        }
        desktopMain.dependsOn(androidAndDesktop)
        androidMain.get().dependsOn(androidAndDesktop)

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
            implementation(libs.squareup.okio.fakefilesystem)
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

    sourceSets["main"].apply {
        res.srcDirs("src/androidMain/res", "src/commonMain/resources/res")
        assets.srcDirs("src/commonMain/resources/assets")
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.techbeloved.hymnbook")
        }
        // needs to be set otherwise, with cocoapods, iosApp fails to build.
        // See https://github.com/cashapp/sqldelight/issues/1442
        linkSqlite = true
    }
}
