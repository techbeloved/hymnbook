import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.detekt)
}

kotlin {
    androidLibrary {
        namespace = "com.techbeloved.media"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        minSdk = libs.versions.android.minSdk.get().toInt()
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    jvmToolchain(libs.versions.jdk.get().toInt())


    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "media"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "media"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "media.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.compose.activity)
            implementation(libs.bundles.media3)
            implementation(libs.coroutines.guava)
        }
        commonMain.dependencies {
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.material.icons)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.preview)
            implementation(libs.jetbrains.material.compose)
            implementation(libs.kotlinx.collections.immutable)
        }
        iosMain.dependencies {
            implementation(libs.kaluga.base)
            implementation(libs.kaluga.media)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.coroutines.swing)
        }
    }
}

detekt {
    config.setFrom(file("../../config/detekt/detekt.yml"))
    source.setFrom(
        "src/androidMain/kotlin",
        "src/androidUnitTest/kotlin",
        "src/commonMain/kotlin",
        "src/commonTest/kotlin",
        "src/desktopMain/kotlin",
        "src/desktopTest/kotlin",
        "src/iosMain/kotlin",
        "src/iosTest/kotlin",
        "src/wasmJsMain/kotlin",
    )
}
