package com.techbeloved.hymnbook.shared.ui.songs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.analytics.TrackAnalyticsEventUseCase
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.titles.GetFilteredSongTitlesUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal class FilteredSongsViewModel @Inject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val getFilteredSongTitlesUseCase: GetFilteredSongTitlesUseCase,
    private val trackAnalyticsEventUseCase: TrackAnalyticsEventUseCase,
) : ViewModel() {
    private val args = savedStateHandle.toRoute<FilteredSongsScreen>()
    private val songFilter = args.songFilter

    val state: StateFlow<FilteredSongsState> = flow {
        val songs = getFilteredSongTitlesUseCase.invoke(songFilter)
        emit(
            FilteredSongsState(
                filter = songFilter,
                songs = songs.toImmutableList(),
                title = args.title,
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = FilteredSongsState(
            filter = songFilter,
            songs = emptyList<SongTitle>().toImmutableList(),
            title = args.title,
        ),
    )

    fun onScreenLoaded() {
       viewModelScope.launch {
           trackAnalyticsEventUseCase(FilteredSongsAnalytics.screenView(songFilter))
       }
    }

    @Inject
    class Factory(val create: (SavedStateHandle) -> FilteredSongsViewModel)

    companion object {
        val Factory = viewModelFactory {
            initializer<FilteredSongsViewModel> {
                val savedStateHandle = createSavedStateHandle()
                appComponent.filteredSongsViewModelFactory().create(savedStateHandle)
            }
        }
    }
}
