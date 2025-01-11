package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.techbeloved.hymnbook.shared.model.SongDisplayMode

internal data class SongPreferences(
    val songDisplayMode: SongDisplayMode,
    val isPreferMidi: Boolean,
) {
    companion object {
        val songDisplayModePrefKey = PreferenceKey(
            inMemory = true,
            key = stringPreferencesKey("song.DisplayMode"),
        )

        val isPreferMidiPrefKey = PreferenceKey(
            inMemory = true,
            key = booleanPreferencesKey("song.IsPreferMidi")
        )
    }
}
