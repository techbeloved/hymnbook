package com.techbeloved.hymnbook.shared.songbooks

import androidx.datastore.preferences.core.stringPreferencesKey
import com.techbeloved.hymnbook.shared.preferences.PreferenceKey

internal val SongbookPreferenceKey = PreferenceKey(
    inMemory = false,
    key = stringPreferencesKey("songbook.preferred"),
    defaultValue = "",
)
