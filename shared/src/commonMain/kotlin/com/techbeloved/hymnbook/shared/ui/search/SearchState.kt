package com.techbeloved.hymnbook.shared.ui.search

import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.collections.immutable.ImmutableList

internal sealed interface SearchState {
    data object Default : SearchState

    data object SearchLoading : SearchState

    // data class RecentSearch

    // data class Suggestions

    data class SearchResult(val titles: ImmutableList<SongTitle>) : SearchState

    data class NoResult(val query: String) : SearchState
}
