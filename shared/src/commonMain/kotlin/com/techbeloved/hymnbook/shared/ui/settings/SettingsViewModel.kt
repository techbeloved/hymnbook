package com.techbeloved.hymnbook.shared.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techbeloved.hymnbook.shared.preferences.GetPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.settings.DarkModePreference
import com.techbeloved.hymnbook.shared.settings.DarkModePreferenceKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

internal class SettingsViewModel @Inject constructor(
    getPreferenceFlowUseCase: GetPreferenceFlowUseCase,
) : ViewModel() {

    val darkModePref =
        getPreferenceFlowUseCase(DarkModePreferenceKey).map { DarkModePreference.valueOf(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = DarkModePreference.System,
            )

}
