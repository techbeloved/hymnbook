package com.techbeloved.hymnbook.shared.ui.search

import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class SearchState(
    val results: ImmutableList<SongTitle> = persistentListOf(),
    val query: String = "",
    val selectedSongbook: String? = null,
    val songbooks: ImmutableList<String> = persistentListOf(),
    val recentSearches: RecentSearches = RecentSearches(),
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val searchSuggestions: SearchSuggestion = SearchSuggestion(
        suggestions = persistentListOf(),
        history = persistentListOf(),
    ),
)
