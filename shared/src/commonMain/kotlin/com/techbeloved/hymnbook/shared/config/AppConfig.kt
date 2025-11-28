package com.techbeloved.hymnbook.shared.config

internal expect val defaultAppConfig: AppConfig

internal data class AppConfig(
    val enforceCopyrightRestriction: Boolean,
)
