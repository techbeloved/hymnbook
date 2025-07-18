package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.model.SongBookEntry
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.media.AudioItem
import kotlinx.collections.immutable.ImmutableList

internal sealed interface SongDetailPagerState {
    data object Loading : SongDetailPagerState

    data class Content(
        val initialPage: Int,
        val pageCount: Int,
        val audioItem: AudioItem?,
        val currentSongId: Long,
        val currentSongBookEntry: SongBookEntry?,
        val currentDisplayMode: SongDisplayMode,
        val displayModes: ImmutableList<SongDisplayModeState>,
        val isSheetMusicAvailableForCurrentItem: Boolean,
        val pages: ImmutableList<Long>,
    ) : SongDetailPagerState
}
