package com.techbeloved.hymnbook.shared.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.media.GetAvailableMediaForSongUseCase
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SongDetailPagerModel(
    private val songbook: String,
    private val entry: String,
    private val getSongEntriesForSongbookUseCase: GetSongEntriesForSongbookUseCase = GetSongEntriesForSongbookUseCase(),
    private val getAvailableMediaForSongUseCase: GetAvailableMediaForSongUseCase = GetAvailableMediaForSongUseCase(),
) : ScreenModel {

    val state = MutableStateFlow<SongDetailPagerState>(SongDetailPagerState.Loading)

    private val preferredMidi = MutableStateFlow(true)

    init {
        screenModelScope.launch {
            val songbookEntry = SongBookEntry(songbook, entry)
            val songEntries = getSongEntriesForSongbookUseCase(songbookEntry)
            val initialSongEntry = songEntries.first { it.songBook == songbookEntry }
            val availableMedia = getAvailableMediaForSongUseCase(initialSongEntry.id)
            println("Available media $availableMedia")
            state.update {
                SongDetailPagerState.Content(
                    initialPage = songEntries.indexOfFirst { it.songBook == songbookEntry },
                    pageCount = songEntries.size,
                    pages = songEntries,
                    audioItem = availableMedia.firstOrNull { if (preferredMidi.value) it.isMidi() else true }
                        ?: availableMedia.firstOrNull(),
                    currentEntry = initialSongEntry,
                )
            }
        }
    }

    fun onPageSelected(pageIndex: Int) {
        when (val currentState = state.value) {
            is SongDetailPagerState.Content -> {
                val pageEntry = currentState.pages[pageIndex]
                screenModelScope.launch {
                    val availableMedia = getAvailableMediaForSongUseCase(pageEntry.id)
                    state.updateIfContent {
                        it.copy(
                            audioItem = availableMedia.firstOrNull { item ->
                                if (preferredMidi.value) item.isMidi() else true
                            } ?: availableMedia.firstOrNull(),
                            currentEntry = pageEntry,
                        )
                    }
                }
            }

            SongDetailPagerState.Loading -> {
                // nothing to do
            }
        }
    }

    private fun MutableStateFlow<SongDetailPagerState>.updateIfContent(
        block: (SongDetailPagerState.Content) -> SongDetailPagerState,
    ) {
        update {
            when (it) {
                is SongDetailPagerState.Content -> block(it)
                SongDetailPagerState.Loading -> it
            }
        }
    }
}
