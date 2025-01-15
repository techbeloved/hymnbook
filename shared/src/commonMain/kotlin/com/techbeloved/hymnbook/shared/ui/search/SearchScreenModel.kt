package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.search.SearchSongsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SearchScreenModel(
    private val searchSongsUseCase: SearchSongsUseCase = SearchSongsUseCase(),
) : ScreenModel {

    var searchQuery by mutableStateOf("")
        private set

    private val _state: MutableStateFlow<SearchState> = MutableStateFlow(SearchState.Default)
    val state = _state.asStateFlow()

    fun onNewQuery(newQuery: String) {
        searchQuery = newQuery
        if (newQuery.isEmpty()) {
            onClearResults()
        }
    }

    fun onSearch() {
        if (searchQuery.isBlank()) return
        _state.update { SearchState.SearchLoading }
        screenModelScope.launch {
            val results = searchSongsUseCase(searchQuery)
            _state.update {
                if (results.isNotEmpty()) SearchState.SearchResult(results.toImmutableList())
                else SearchState.NoResult(searchQuery)
            }
        }
    }

    private fun onClearResults() {
        _state.update { SearchState.Default }
    }
}
