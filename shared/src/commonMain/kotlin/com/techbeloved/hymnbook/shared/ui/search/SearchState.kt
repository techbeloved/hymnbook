package com.techbeloved.hymnbook.shared.ui.search

import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class SearchState(
    val results: ImmutableList<SongTitle> = persistentListOf(),
    val query: String = "",
    val selectedSongbook: String? = null,
    val songbooks: ImmutableList<String> = persistentListOf(),
    val recentSongs: ImmutableList<SongTitle> = persistentListOf(),
    val recentSearches: ImmutableList<String> = persistentListOf(),
    val isLoading: Boolean = false,
)
