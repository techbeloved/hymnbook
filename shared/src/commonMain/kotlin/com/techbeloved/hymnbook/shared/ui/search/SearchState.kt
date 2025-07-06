package com.techbeloved.hymnbook.shared.ui.search

import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.collections.immutable.ImmutableList

internal data class SearchState(
    val results: ImmutableList<SongTitle>,
    val query: String,
    val selectedSongbook: String?,
    val songbooks: ImmutableList<String>,
    val isLoading: Boolean,
)
