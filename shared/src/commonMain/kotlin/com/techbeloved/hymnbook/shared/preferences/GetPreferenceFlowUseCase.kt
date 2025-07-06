package com.techbeloved.hymnbook.shared.preferences

import me.tatarka.inject.annotations.Inject

internal class GetPreferenceFlowUseCase @Inject constructor(
    private val repository: PreferencesRepository,
) {
    operator fun <T> invoke(preferenceKey: PreferenceKey<T>) = repository.getPreferenceFlow(preferenceKey)
}
