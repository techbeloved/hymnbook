package com.techbeloved.hymnbook.shared.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.media.GetAvailableMediaForSongUseCase
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.PreferencesRepository
import com.techbeloved.hymnbook.shared.sheetmusic.GetAvailableSheetMusicForSongUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SongDetailPagerModel(
    songbook: String,
    entry: String,
    private val getSongEntriesForSongbookUseCase: GetSongEntriesForSongbookUseCase = GetSongEntriesForSongbookUseCase(),
    private val getAvailableMediaForSongUseCase: GetAvailableMediaForSongUseCase = GetAvailableMediaForSongUseCase(),
    private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase =
        GetAvailableSheetMusicForSongUseCase(),
    private val preferencesRepository: PreferencesRepository = Injector.preferencesRepository,
) : ScreenModel {

    private val initialSongbookEntry = SongBookEntry(songbook, entry)

    private val selectedPage = MutableStateFlow(-1)

    val state = combine(
        preferencesRepository.songPreferences,

        getSongEntriesFlow().map { songEntries ->
            object {
                val initialPage = songEntries.indexOfFirst { it.songBook == initialSongbookEntry }
                val songEntries = songEntries
            }
        },
        selectedPage
    ) { preferences, songEntries, selectedPageIndex ->

        val currentIndex = if (selectedPageIndex < 0) songEntries.initialPage else selectedPageIndex
        val currentEntry = songEntries.songEntries[currentIndex]
        val availableMedia = getAvailableMediaForSongUseCase(currentEntry.id)
        val availableSheetMusic = getAvailableSheetMusicForSongUseCase(currentEntry.id)

        SongDetailPagerState.Content(
            initialPage = songEntries.initialPage,
            pageCount = songEntries.songEntries.size,
            isSheetMusicAvailableForCurrentItem = availableSheetMusic != null,

            audioItem = availableMedia.firstOrNull { item ->
                if (preferences.isPreferMidi) item.isMidi() else true
            } ?: availableMedia.firstOrNull(),

            currentEntry = currentEntry,

            pages = songEntries.songEntries,
            displayModes = SongDisplayMode.entries.map { mode ->
                SongDisplayModeState(
                    displayMode = mode,
                    isEnabled = when (mode) {
                        SongDisplayMode.Lyrics -> true
                        SongDisplayMode.SheetMusic -> availableSheetMusic != null
                    },
                    text = when (mode) {
                        SongDisplayMode.Lyrics -> "Lyrics"
                        SongDisplayMode.SheetMusic -> "Sheet"
                    },
                )
            }.toImmutableList(),
            currentDisplayMode = preferences.songDisplayMode,
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SongDetailPagerState.Loading,
    )

    private fun getSongEntriesFlow() =
        flow { emit(getSongEntriesForSongbookUseCase(initialSongbookEntry)) }

    fun onPageSelected(pageIndex: Int) {
        selectedPage.update { pageIndex }
    }

    fun onChangeSongDisplayMode(songDisplayMode: SongDisplayMode) {
        screenModelScope.launch {
            preferencesRepository.updateSongPreference(songDisplayMode)
        }
    }
}
