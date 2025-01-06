package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.model.SongPageEntry
import com.techbeloved.media.AudioItem
import kotlinx.collections.immutable.ImmutableList

internal sealed interface SongDetailPagerState {
    data object Loading : SongDetailPagerState

    data class Content(
        val initialPage: Int,
        val pageCount: Int,
        val audioItem: AudioItem?,
        val currentEntry: SongPageEntry,
        val pages: ImmutableList<SongPageEntry>,
    ) : SongDetailPagerState
}
