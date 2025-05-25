package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.techbeloved.hymnbook.shared.model.SongDisplayMode

internal data class SongPreferences(
    val songDisplayMode: SongDisplayMode,
    val isPreferMidi: Boolean,
    val fontSize: Float,
) {
    companion object {

        const val DEFAULT_FONT_SIZE = 20f
        const val MIN_FONT_SIZE = 16f
        const val MAX_FONT_SIZE = 28f
        const val FONT_CHANGE_STEP = 1f

        val songDisplayModePrefKey = PreferenceKey(
            inMemory = true,
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
            defaultValue = 20f,
        )
    }
}
