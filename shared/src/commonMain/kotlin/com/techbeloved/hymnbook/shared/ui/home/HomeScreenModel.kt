package com.techbeloved.hymnbook.shared.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.SongbookEntity
import com.techbeloved.hymnbook.shared.analytics.TrackAnalyticsEventUseCase
import com.techbeloved.hymnbook.shared.assetimport.ImportBundledAssetsUseCase
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.preferences.ChangePreferenceUseCase
import com.techbeloved.hymnbook.shared.preferences.GetPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.songbooks.GetAllSongbooksUseCase
import com.techbeloved.hymnbook.shared.songbooks.SongbookPreferenceKey
import com.techbeloved.hymnbook.shared.titles.GetFilteredSongTitlesUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

internal class HomeScreenModel @Inject constructor(
    private val getFilteredSongTitlesUseCase: GetFilteredSongTitlesUseCase,
    private val importBundledAssetsUseCase: ImportBundledAssetsUseCase,
    private val trackAnalyticsEventUseCase: TrackAnalyticsEventUseCase,
    private val changePreferenceUseCase: ChangePreferenceUseCase,
    getPreferenceFlowUseCase: GetPreferenceFlowUseCase,
    getAllSongbooksUseCase: GetAllSongbooksUseCase,
) : ViewModel() {

    private val assetsReady = MutableStateFlow(false)
    private val sortBy = MutableStateFlow(value = SortBy.Number)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedSongbook = getPreferenceFlowUseCase(SongbookPreferenceKey)
        .flatMapLatest { preference ->
            getAllSongbooksUseCase().map { songbooks ->
                songbooks.firstOrNull { songbook -> songbook.name == preference }
            }
        }
    private val songbooks = getAllSongbooksUseCase().map { it.toImmutableList() }

    val state = combine(
        assetsReady,
        sortBy,
        selectedSongbook,
        songbooks,
    ) { assetsReady, sortBy, selectedSongbook, songbooks ->
        if (assetsReady) {
            val songbook = selectedSongbook ?: songbooks.firstOrNull()
            HomeScreenState(
                songTitles = getFilteredSongTitlesUseCase(
                    songFilter = SongFilter.songbookFilter(
                        songbook = songbook?.name.orEmpty(),
                        sortByTitle = sortBy == SortBy.Title,
                    )
                ).toImmutableList(),
                songbooks = songbooks,
                currentSongbook = selectedSongbook ?: songbook,
                isLoading = false,
                sortBy = sortBy,
            )
        } else {
            HomeScreenState.EmptyLoading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeScreenState.EmptyLoading,
    )

    init {
        viewModelScope.launch {
            importBundledAssetsUseCase()
            assetsReady.update { true }
        }
    }

    fun onUpdateSortBy(sortBy: SortBy) {
        this.sortBy.update { sortBy }
        viewModelScope.launch {
            trackAnalyticsEventUseCase(
                event = HomeAnalytics.actionUpdateSortBy(value = sortBy.name)
            )
        }
    }

    fun onUpdateSongbook(songbook: SongbookEntity) {
        viewModelScope.launch {
            changePreferenceUseCase(SongbookPreferenceKey) {
                songbook.name
            }
            trackAnalyticsEventUseCase(
                event = HomeAnalytics.actionSelectSongbook(value = songbook.name)
            )
        }
    }

    fun onScreenLoaded() {
        viewModelScope.launch {
            trackAnalyticsEventUseCase(HomeAnalytics.screenView)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { appComponent.homeScreenModel() }
        }
    }
}
