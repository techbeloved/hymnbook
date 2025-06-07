package com.techbeloved.hymnbook.shared.ui.home

import com.techbeloved.hymnbook.SongbookEntity
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class HomeScreenState(
    val songTitles: ImmutableList<SongTitle>,
    val songbooks: ImmutableList<SongbookEntity>,
    val currentSongbook: SongbookEntity?,
    val isLoading: Boolean,
    val sortBy: SortBy,
) {

    companion object {
        val EmptyLoading = HomeScreenState(
            songTitles = persistentListOf(),
            songbooks = persistentListOf(),
            currentSongbook = null,
            isLoading = true,
            sortBy = SortBy.Number,
        )
    }
}
