package com.techbeloved.hymnbook.shared.ui.songs

import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.collections.immutable.ImmutableList

internal data class FilteredSongsState(
    val filter: SongFilter,
    val songs: ImmutableList<SongTitle>,
    val title: String,
)
