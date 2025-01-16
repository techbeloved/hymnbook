package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.preferences.core.Preferences

internal data class PreferenceKey<T>(
    val inMemory: Boolean,
    val key: Preferences.Key<T>,
    val defaultValue: T,
)
