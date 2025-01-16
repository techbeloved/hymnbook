package com.techbeloved.hymnbook.shared.preferences

import com.techbeloved.hymnbook.shared.di.Injector

internal class ChangePreferenceUseCase(private val repository: PreferencesRepository = Injector.preferencesRepository) {
    suspend operator fun <T> invoke(
        preferenceKey: PreferenceKey<T>,
        block: suspend (oldValue: T) -> T,
    ) = repository.updatePreference(preferenceKey, block)
}
