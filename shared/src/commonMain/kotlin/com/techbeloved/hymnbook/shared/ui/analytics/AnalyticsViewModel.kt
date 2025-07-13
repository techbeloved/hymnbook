package com.techbeloved.hymnbook.shared.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.analytics.SetAnalyticsDefaultParametersUseCase
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.preferences.GetPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.preferences.GetSongPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.songbooks.SongbookPreferenceKey
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

internal class AnalyticsViewModel @Inject constructor(
    private val setAnalyticsDefaultParametersUseCase: SetAnalyticsDefaultParametersUseCase,
    preferenceFlowUseCase: GetPreferenceFlowUseCase,
    getSongPreferenceFlowUseCase: GetSongPreferenceFlowUseCase,
): ViewModel() {

    val analyticsTracking = combine(
        getSongPreferenceFlowUseCase(),
        preferenceFlowUseCase(SongbookPreferenceKey),
    ) { songPreferences, songbookPrefs ->
        mapOf(
            "song_display_mode" to songPreferences.songDisplayMode.name,
            "song_font_size" to songPreferences.fontSize.toString(),
            "preferred_songbook" to songbookPrefs,
        )
    }

    fun logDefaultParameters(parameters: Map<String, String>) {
        viewModelScope.launch {
            setAnalyticsDefaultParametersUseCase(parameters)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { appComponent.analyticsViewModel() }
        }
    }
}
