package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.techbeloved.hymnbook.shared.model.SongDisplayMode

internal data class SongPreferences(
    val songDisplayMode: SongDisplayMode,
    val isPreferMidi: Boolean,
    val fontSize: Float,
    val isCompactDisplay: Boolean,
) {
    companion object {

        const val DEFAULT_FONT_SIZE = 1f
        const val MIN_FONT_SIZE = .7f
        const val MAX_FONT_SIZE = 2.25f
        const val FONT_CHANGE_STEP = 1.15f

        val songDisplayModePrefKey = PreferenceKey(
            inMemory = false,
            key = stringPreferencesKey("song.DisplayMode"),
            defaultValue = SongDisplayMode.Lyrics.name,
        )

        val isPreferMidiPrefKey = PreferenceKey(
            inMemory = true,
            key = booleanPreferencesKey("song.IsPreferMidi"),
            defaultValue = true,
        )

        val songFontSizePrefKey = PreferenceKey(
            inMemory = false,
            key = floatPreferencesKey("song.FontSize"),
            defaultValue = DEFAULT_FONT_SIZE,
        )
        val songCompactDisplayPrefKey = PreferenceKey(
            inMemory = false,
            key = booleanPreferencesKey("song.CompactDisplay"),
            defaultValue = false,
        )
    }
}
