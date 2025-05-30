package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Inject

internal class PreferencesRepository @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>,
    private val inMemoryPreferencesDataStore: DataStore<InMemoryPreferences>,
) {

    fun <T> getPreferenceFlow(preferenceKey: PreferenceKey<T>): Flow<T> = combine(
        preferencesDataStore.data,
        inMemoryPreferencesDataStore.data,
    ) { persistedPreferences, inMemoryPreferences ->
        getPreferenceValue(persistedPreferences, inMemoryPreferences, preferenceKey)
            ?: preferenceKey.defaultValue
    }

    /**
     * Update the given preference
     * @param preferenceKey is the preference key
     * @param block receives the current value and return the updated value
     */
    suspend fun <T> updatePreference(
        preferenceKey: PreferenceKey<T>,
        block: suspend (oldValue: T) -> T,
    ) {
        if (preferenceKey.inMemory) {
            inMemoryPreferencesDataStore.edit {
                it[preferenceKey.key] = block(it[preferenceKey.key] ?: preferenceKey.defaultValue)
            }
        } else {
            preferencesDataStore.edit {
                it[preferenceKey.key] = block(it[preferenceKey.key] ?: preferenceKey.defaultValue)
            }
        }
    }

    private fun <T> getPreferenceValue(
        persistedPreferences: Preferences,
        inMemoryPreferences: InMemoryPreferences,
        key: PreferenceKey<T>,
    ): T? {
        return if (key.inMemory) inMemoryPreferences[key.key] else persistedPreferences[key.key]
    }
}
