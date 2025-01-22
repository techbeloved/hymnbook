plugins {
    alias(libs.plugins.jetbrains.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.detekt)
    application
}

group = "com.techbeloved.hymnbook"
version = "1.0.0"
application {
    mainClass.set("com.techbeloved.hymnbook.ApplicationKt")
    applicationDefaultJvmArgs =
        listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {

    implementation(libs.logback)
    implementation(libs.slf4j.api)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.status.pages)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.test.junit)
}
