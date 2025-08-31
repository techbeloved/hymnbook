package com.techbeloved.hymnbook.shared.soundfont

import androidx.datastore.preferences.core.stringPreferencesKey
import com.techbeloved.hymnbook.shared.preferences.PreferenceKey

internal val SoundFontPreferenceKey = PreferenceKey(
    inMemory = false,
    key = stringPreferencesKey("soundfont.preferred"),
    defaultValue = "",
)
