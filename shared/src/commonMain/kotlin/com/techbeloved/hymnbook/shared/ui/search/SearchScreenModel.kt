package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.search.SearchSongsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

internal class SearchScreenModel @Inject constructor(
    private val searchSongsUseCase: SearchSongsUseCase,
) : ViewModel() {

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
        viewModelScope.launch {
            val results = searchSongsUseCase(searchQuery)
            _state.update {
                if (results.isNotEmpty()) {
                    SearchState.SearchResult(results.toImmutableList())
                } else {
                    SearchState.NoResult(searchQuery)
                }
            }
        }
    }

    private fun onClearResults() {
        _state.update { SearchState.Default }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                appComponent.searchScreenModel()
            }
        }
    }
}
