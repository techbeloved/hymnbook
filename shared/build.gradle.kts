
import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.about.libraries)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.hotreload)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
    }
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
    applyDefaultHierarchyTemplate()

    explicitApi()

    sourceSets {
        dependencies.ksp(libs.kotlin.inject.compiler)
        dependencies.ksp(libs.kotlin.inject.anvil.compiler)
        commonMain.dependencies {
            implementation(project(":modules:api-client"))
            implementation(project(":modules:media"))
            implementation(project(":modules:sheetmusic"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.jetbrains.material.compose)
            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.jetbrains.viewmodel.compose)
            implementation(libs.jetbrains.runtime.compose)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.io)
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines)

            // About libraries
            implementation(libs.about.libraries.compose.core)
            implementation(libs.about.libraries.compose.m3)
            implementation(libs.about.libraries.core)

            // Sqldelight
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.adapters)

            // okio
            implementation(libs.squareup.okio)

            // xmlutil
            implementation(libs.xmlutil.core)
            implementation(libs.xmlutil.serialization)

            // Haze
            implementation(libs.haze)
            implementation(libs.haze.materials)

            // datastore
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferences)

            // Inject
            implementation(libs.kotlin.inject.runtime)
            implementation(libs.kotlin.inject.anvil.runtime)
            implementation(libs.kotlin.inject.anvil.runtime.optional)
        }

        androidMain.dependencies {
            api(libs.compose.activity)
            implementation(libs.sqldelight.android)
            implementation(compose.preview)
            implementation(compose.uiTooling)
            implementation(compose.ui)
            // Requery Sqlite
            implementation(libs.requery.sqlite.android)
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
            implementation(libs.cashapp.turbine)
            implementation(libs.squareup.okio.fakefilesystem)
        }

        androidUnitTest.dependencies {
            implementation(libs.sqldelight.jvm)
        }
    }
}

compose.resources {
    packageOfResClass = "com.techbeloved.hymnbook.shared.generated"
    generateResClass = auto
    publicResClass = false
}

android {
    namespace = "com.techbeloved.hymnbook.shared"
    compileSdk = libs.versions.android.targetSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    kotlin {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }

    sourceSets["main"].apply {
        res.srcDirs("src/androidMain/res")
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

aboutLibraries {
    export {
        outputFile = file("src/commonMain/composeResources/files/about/libraries.json")
        prettyPrint = true
    }

    library {
        duplicationMode = DuplicateMode.MERGE
        duplicationRule = DuplicateRule.SIMPLE
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.techbeloved.hymnbook")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
        // needs to be set otherwise, with cocoapods, iosApp fails to build.
        // See https://github.com/cashapp/sqldelight/issues/1442
        linkSqlite = true
    }
}

detekt {
    config.setFrom(file("../config/detekt/detekt.yml"))
    source.setFrom(
        "src/androidAndDesktop/kotlin",
        "src/androidMain/kotlin",
        "src/androidUnitTest/kotlin",
        "src/commonMain/kotlin",
        "src/commonTest/kotlin",
        "src/desktopMain/kotlin",
        "src/desktopTest/kotlin",
        "src/iosMain/kotlin",
        "src/iosTest/kotlin",
    )
}
