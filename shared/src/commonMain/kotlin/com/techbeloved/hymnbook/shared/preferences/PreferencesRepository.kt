package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.detail.SettingsPreferenceAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class PreferencesRepository(
    private val preferencesDataStore: DataStore<Preferences> = Injector.preferencesDataStore,
    private val inMemoryPreferencesDataStore: DataStore<InMemoryPreferences> = Injector.inMemoryDataStore,
) {

    val songPreferences: Flow<SongPreferences> = combine(
        preferencesDataStore.data,
        inMemoryPreferencesDataStore.data,
    ) { persistedPreferences, inMemoryPreferences ->
        SongPreferences(
            isPreferMidi = getPreferenceValue(
                persistedPreferences = persistedPreferences,
                inMemoryPreferences = inMemoryPreferences,
                key = SongPreferences.isPreferMidiPrefKey
            ) ?: false,

            songDisplayMode = getPreferenceValue(
                persistedPreferences = persistedPreferences,
                inMemoryPreferences = inMemoryPreferences,
                key = SongPreferences.songDisplayModePrefKey
            )?.let(SongDisplayMode::valueOf) ?: SongDisplayMode.Lyrics,

            fontSize = getPreferenceValue(
                persistedPreferences = persistedPreferences,
                inMemoryPreferences = inMemoryPreferences,
                key = SongPreferences.songFontSizePrefKey,
            ) ?: SongPreferences.DEFAULT_FONT_SIZE,
        )
    }

    suspend fun updateSongPreference(songDisplayMode: SongDisplayMode) {
        updatePreference(SongPreferences.songDisplayModePrefKey, songDisplayMode.name)
    }

    suspend fun updateSongPreference(isPreferMidi: Boolean) {
        updatePreference(SongPreferences.isPreferMidiPrefKey, isPreferMidi)
    }

    suspend fun updatePreference(preferenceAction: SettingsPreferenceAction) {
        when (preferenceAction) {
            is SettingsPreferenceAction.FontSize -> {
                updateFontSize(preferenceAction)
            }
        }
    }

    private suspend fun updateFontSize(preferenceAction: SettingsPreferenceAction.FontSize) {
        preferencesDataStore.edit { preference ->
            val currentValue = preference[SongPreferences.songFontSizePrefKey.key]
                ?: SongPreferences.DEFAULT_FONT_SIZE

            val change =
                if (preferenceAction is SettingsPreferenceAction.FontSize.Decrease) {
                    -SongPreferences.FONT_CHANGE_STEP
                } else {
                    SongPreferences.FONT_CHANGE_STEP
                }
            val updatedFontSize = (currentValue + change).coerceIn(
                minimumValue = SongPreferences.MIN_FONT_SIZE,
                maximumValue = SongPreferences.MAX_FONT_SIZE,
            )
            preference[SongPreferences.songFontSizePrefKey.key] = updatedFontSize
        }
    }

    private fun <T> getPreferenceValue(
        persistedPreferences: Preferences,
        inMemoryPreferences: InMemoryPreferences,
        key: PreferenceKey<T>,
    ): T? {
        return if (key.inMemory) inMemoryPreferences[key.key] else persistedPreferences[key.key]
    }

    private suspend fun <T> updatePreference(
        preferenceKey: PreferenceKey<T>,
        newValue: T,
    ) {
        if (preferenceKey.inMemory) {
            inMemoryPreferencesDataStore.edit { it[preferenceKey.key] = newValue }
        } else {
            preferencesDataStore.edit { it[preferenceKey.key] = newValue }
        }
    }
}
