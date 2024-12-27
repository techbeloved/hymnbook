package com.techbeloved.hymnbook.shared.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SongDetailPagerModel(
    private val songbook: String,
    private val entry: String,
    private val getSongEntriesForSongbookUseCase: GetSongEntriesForSongbookUseCase = GetSongEntriesForSongbookUseCase(),
) : ScreenModel {

    val state = MutableStateFlow<SongDetailPagerState>(SongDetailPagerState.Loading)

    init {
        screenModelScope.launch {
            val songbookEntry = SongBookEntry(songbook, entry)
            val songEntries = getSongEntriesForSongbookUseCase(songbookEntry)
            state.update {
                SongDetailPagerState.Content(
                    initialPage = songEntries.indexOfFirst { it.songBook == songbookEntry },
                    pageCount = songEntries.size,
                    pages = songEntries,
                )
            }
        }
    }
}