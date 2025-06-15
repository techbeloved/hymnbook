package com.techbeloved.hymnbook.shared.preferences

import me.tatarka.inject.annotations.Inject

internal class ChangeFontSizeUseCase @Inject constructor(
    private val repository: PreferencesRepository,
) {
    suspend operator fun invoke(isIncrease: Boolean) {
        repository.updatePreference(SongPreferences.songFontSizePrefKey) { oldValue ->
            val change = if (isIncrease) {
                oldValue * SongPreferences.FONT_CHANGE_STEP
            } else {
                oldValue / SongPreferences.FONT_CHANGE_STEP
            }
            val updatedFontSizeMultiplier = (change).coerceIn(
                minimumValue = SongPreferences.MIN_FONT_SIZE,
                maximumValue = SongPreferences.MAX_FONT_SIZE,
            )
            updatedFontSizeMultiplier
        }
    }
}
