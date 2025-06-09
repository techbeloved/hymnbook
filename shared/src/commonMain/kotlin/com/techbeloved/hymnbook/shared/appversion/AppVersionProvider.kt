package com.techbeloved.hymnbook.shared.appversion

internal fun interface AppVersionProvider {
    fun get(): AppVersion
}

internal expect val defaultAppVersionProvider: AppVersionProvider

public data class AppVersion(
    val name: String,
    val code: String,
)
