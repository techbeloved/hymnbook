package com.techbeloved.hymnbook.shared.preferences

import com.techbeloved.hymnbook.shared.di.Injector

internal class ChangeFontSizeUseCase(
    private val repository: PreferencesRepository = Injector.preferencesRepository,
) {
    suspend operator fun invoke(isIncrease: Boolean) {
        repository.updatePreference(SongPreferences.songFontSizePrefKey) { oldValue ->
            val change = if (isIncrease) {
                SongPreferences.FONT_CHANGE_STEP
            } else {
                -SongPreferences.FONT_CHANGE_STEP
            }
            val updatedFontSize = (oldValue + change).coerceIn(
                minimumValue = SongPreferences.MIN_FONT_SIZE,
                maximumValue = SongPreferences.MAX_FONT_SIZE,
            )
            updatedFontSize
        }
    }
}
