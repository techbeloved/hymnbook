@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.techbeloved.hymnbook.shared.search.FilterSearchHistoryFlowUseCase
import com.techbeloved.hymnbook.shared.search.GetLatestSearchHistoryFlowUseCase
import com.techbeloved.hymnbook.shared.search.GetRecentlyViewedSongTitlesFlowUseCase
import com.techbeloved.hymnbook.shared.search.GetSearchSuggestionsUseCase
import com.techbeloved.hymnbook.shared.search.SaveSearchQueryToSearchHistoryUseCase
import com.techbeloved.hymnbook.shared.search.SearchSongsUseCase
import com.techbeloved.hymnbook.shared.songbooks.GetAllSongbooksUseCase
import com.techbeloved.hymnbook.shared.songbooks.GetPreferredSongbookUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal class SearchScreenModel @Inject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val searchSongsUseCase: SearchSongsUseCase,
    private val getPreferredSongbookUseCase: GetPreferredSongbookUseCase,
    private val trackAnalyticsUseCase: TrackAnalyticsEventUseCase,
    private val saveSearchQueryToSearchHistoryUseCase: SaveSearchQueryToSearchHistoryUseCase,
    songbooksUseCase: GetAllSongbooksUseCase,
    filterSearchHistoryUseCase: FilterSearchHistoryFlowUseCase,
    getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase,
    getLatestSearchHistoryUseCase: GetLatestSearchHistoryFlowUseCase,
    getRecentlyViewedSongbooksUseCase: GetRecentlyViewedSongTitlesFlowUseCase,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<SearchScreen>()

    var searchQuery by mutableStateOf("")
        private set

    private val _state: MutableStateFlow<SearchState> = MutableStateFlow(initialState)
    private val searchQueryFlow = snapshotFlow { searchQuery }

    private val debouncedSearchQuery = searchQueryFlow.debounce(10)


    private val instantResultsFlow: Flow<List<SongTitle>> = if (args.isSpeedDial) {
        debouncedSearchQuery.mapLatest { query ->
            val songbook = state.value.selectedSongbook ?: state.value.songbooks.firstOrNull()
            searchSongsUseCase(searchQuery = query, songbook = songbook)
        }
    } else {
        flowOf(emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchSuggestions = combine(
        searchQueryFlow.flatMapLatest { filterSearchHistoryUseCase(it) },
        searchQueryFlow.flatMapLatest { getSearchSuggestionsUseCase(it) },
        instantResultsFlow,
    ) { history, suggestions, instantResults ->
        SearchSuggestion(
            suggestions = suggestions.toImmutableList(),
            history = history.toImmutableList(),
        ) to instantResults
    }

    private val recentSearches = combine(
        getLatestSearchHistoryUseCase(),
        getRecentlyViewedSongbooksUseCase(),
    ) { recentSearches, recentSongs ->
        RecentSearches(
            songs = recentSongs.toImmutableList(),
            searches = recentSearches.toImmutableList(),
        )
    }

    val state = combine(
        songbooksUseCase(),
        recentSearches,
        searchSuggestions,
        _state,
        debouncedSearchQuery,
    ) { songbooks, recentSearches, (suggestions, instantResults), state, currentQuery ->
        state.copy(
            songbooks = songbooks.map { it.name }.toImmutableList(),
            selectedSongbook = state.selectedSongbook ?: getPreferredSongbookUseCase(),
            recentSearches = recentSearches,
            searchSuggestions = suggestions,
            isTyping = currentQuery.isNotEmpty() && !state.isLoading && currentQuery != state.query,
            results = if (args.isSpeedDial) instantResults.toImmutableList() else state.results,
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

    @Inject
    class Factory(val create: (SavedStateHandle) -> SearchScreenModel)

    private val initialState
        get() = SearchState(
            query = "",
            isLoading = false,
            results = persistentListOf(),
            songbooks = persistentListOf(),
            selectedSongbook = null,
            isSpeedDial = args.isSpeedDial,
        )

    companion object {

        val Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                appComponent.searchScreenModelFactory().create(savedStateHandle)
            }
        }
    }
}
