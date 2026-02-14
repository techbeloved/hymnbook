package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.analytics.TrackAnalyticsEventUseCase
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.search.GetLatestSearchHistoryFlowUseCase
import com.techbeloved.hymnbook.shared.search.GetRecentlyViewedSongTitlesFlowUseCase
import com.techbeloved.hymnbook.shared.search.SaveSearchQueryToSearchHistoryUseCase
import com.techbeloved.hymnbook.shared.search.SearchSongsUseCase
import com.techbeloved.hymnbook.shared.songbooks.GetAllSongbooksUseCase
import com.techbeloved.hymnbook.shared.songbooks.GetPreferredSongbookUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

internal class SearchScreenModel @Inject constructor(
    private val searchSongsUseCase: SearchSongsUseCase,
    private val getPreferredSongbookUseCase: GetPreferredSongbookUseCase,
    private val trackAnalyticsUseCase: TrackAnalyticsEventUseCase,
    private val saveSearchQueryToSearchHistoryUseCase: SaveSearchQueryToSearchHistoryUseCase,
    songbooksUseCase: GetAllSongbooksUseCase,
    getLatestSearchHistoryUseCase: GetLatestSearchHistoryFlowUseCase,
    getRecentlyViewedSongbooksUseCase: GetRecentlyViewedSongTitlesFlowUseCase,
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    private val _state: MutableStateFlow<SearchState> = MutableStateFlow(initialState)
    val state = combine(
        songbooksUseCase(),
        getLatestSearchHistoryUseCase(),
        getRecentlyViewedSongbooksUseCase(),
        _state
    ) { songbooks, recentSearches, recentSongs, state ->
        state.copy(
            songbooks = songbooks.map { it.name }.toImmutableList(),
            selectedSongbook = state.selectedSongbook ?: getPreferredSongbookUseCase(),
            recentSearches = recentSearches.toImmutableList(),
            recentSongs = recentSongs.toImmutableList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = initialState,
    )

    fun onNewQuery(newQuery: String) {
        searchQuery = newQuery
        if (newQuery.isEmpty()) {
            onClearResults()
        }
    }

    fun onSearch() {
        if (searchQuery.isBlank()) return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val selectedSongbook =
                state.value.selectedSongbook ?: state.value.songbooks.firstOrNull()
            val results = searchSongsUseCase(searchQuery, selectedSongbook)
            _state.update {
                it.copy(
                    isLoading = false,
                    query = searchQuery,
                    results = results.toImmutableList(),
                    selectedSongbook = selectedSongbook,
                )
            }
            trackAnalyticsUseCase(SearchAnalytics.actionSearch(searchQuery))
            saveSearchQueryToSearchHistoryUseCase(searchQuery)
        }
    }

    fun onFilterBySongbook(songbook: String) {
        _state.update {
            it.copy(
                selectedSongbook = songbook,
            )
        }
        onSearch()
    }

    fun onScreenLoaded() {
        viewModelScope.launch {
            trackAnalyticsUseCase(SearchAnalytics.screenView)
        }
    }

    private fun onClearResults() {
        _state.update {
            it.copy(
                query = "",
                results = persistentListOf(),
                isLoading = false,
            )
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                appComponent.searchScreenModel()
            }
        }

        private val initialState = SearchState(
            query = "",
            isLoading = false,
            results = persistentListOf(),
            songbooks = persistentListOf(),
            selectedSongbook = null,
        )
    }
}
