package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.model.SongBookEntry
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.model.soundfont.SavedSoundFont
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
        val isSheetMusicAvailableForCurrentItem: Boolean,
        val soundFontState: SoundFontState,
        val pages: ImmutableList<Long>,
        val bottomSheetState: DetailBottomSheetState,
    ) : SongDetailPagerState
}

internal sealed interface SoundFontState {
    data object NotSupported : SoundFontState
    data class Available(val soundFont: SavedSoundFont) : SoundFontState

    /**
     * Sound font is supported but not available on device or not downloaded yet. We have to ask the user to download it
     */
    data object NotAvailable : SoundFontState
}
