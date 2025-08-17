package com.techbeloved.hymnbook.shared.ui.soundfonts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.di.appComponent
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject

internal class SoundFontSettingsViewModel @Inject constructor(): ViewModel() {

    val state = MutableStateFlow(SoundFontSettingsState())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                appComponent.soundFontSettingsViewModel()
            }
        }
    }
}
