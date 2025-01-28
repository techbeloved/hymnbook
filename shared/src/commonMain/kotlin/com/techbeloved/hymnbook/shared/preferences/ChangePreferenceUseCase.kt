package com.techbeloved.hymnbook.shared.preferences

import me.tatarka.inject.annotations.Inject

internal class ChangePreferenceUseCase @Inject constructor(
    private val repository: PreferencesRepository,
) {
    suspend operator fun <T> invoke(
        preferenceKey: PreferenceKey<T>,
        block: suspend (oldValue: T) -> T,
    ) = repository.updatePreference(preferenceKey, block)
}
