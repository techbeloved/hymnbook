package com.techbeloved.hymnbook.shared.settings

import androidx.datastore.preferences.core.stringPreferencesKey
import com.techbeloved.hymnbook.shared.preferences.PreferenceKey

internal val DarkModePreferenceKey = PreferenceKey(
    inMemory = false,
    key = stringPreferencesKey("settings.dark_mode"),
    defaultValue = DarkModePreference.System.name,
)

public enum class DarkModePreference {
    Light,
    Dark,
    System
}
