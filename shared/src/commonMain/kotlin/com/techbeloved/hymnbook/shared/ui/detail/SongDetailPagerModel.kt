package com.techbeloved.hymnbook.shared.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.media.GetAvailableMediaForSongUseCase
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.ChangeFontSizeUseCase
import com.techbeloved.hymnbook.shared.preferences.ChangePreferenceUseCase
import com.techbeloved.hymnbook.shared.preferences.GetSongPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.preferences.SongPreferences
import com.techbeloved.hymnbook.shared.sheetmusic.GetAvailableSheetMusicForSongUseCase
import com.techbeloved.hymnbook.shared.songs.GetSongEntriesForSongbookUseCase
import com.techbeloved.hymnbook.shared.ui.settings.NowPlayingBottomSettingsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

internal class SongDetailPagerModel(
    songBookEntry: SongBookEntry,
    private val getSongEntriesForSongbookUseCase: GetSongEntriesForSongbookUseCase,
    private val getAvailableMediaForSongUseCase: GetAvailableMediaForSongUseCase,
    private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase,
    private val changePreferenceUseCase: ChangePreferenceUseCase,
    getSongPreferenceFlowUseCase: GetSongPreferenceFlowUseCase,
    private val changeFontSizeUseCase: ChangeFontSizeUseCase,
) : ScreenModel {

    private val _bottomSheetState =
        MutableStateFlow<DetailBottomSheetState>(DetailBottomSheetState.Hidden)
    val bottomSheetState get() = _bottomSheetState.asStateFlow()

    private val initialSongbookEntry = songBookEntry

    private val selectedPage = MutableStateFlow(-1)
    val state = combine(
        getSongPreferenceFlowUseCase(),

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
            changePreferenceUseCase(SongPreferences.songDisplayModePrefKey) { songDisplayMode.name }
        }
    }

    fun onShowSettings() = _bottomSheetState.update {
        DetailBottomSheetState.Show(NowPlayingBottomSettingsState.Default)
    }

    fun onHideSettings() = _bottomSheetState.update { DetailBottomSheetState.Hidden }

    fun onIncreaseFontSize() {
        screenModelScope.launch {
            changeFontSizeUseCase(isIncrease = true)
        }
    }

    fun onDecreaseFontSize() {
        screenModelScope.launch {
            changeFontSizeUseCase(isIncrease = false)
        }
    }

    class Factory @Inject constructor(
        private val getSongEntriesForSongbookUseCase: GetSongEntriesForSongbookUseCase,
        private val getAvailableMediaForSongUseCase: GetAvailableMediaForSongUseCase,
        private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase,
        private val changePreferenceUseCase: ChangePreferenceUseCase,
        private val getSongPreferenceFlowUseCase: GetSongPreferenceFlowUseCase,
        private val changeFontSizeUseCase: ChangeFontSizeUseCase,
    ) {
        fun create(
            songBookEntry: SongBookEntry,
        ): SongDetailPagerModel = SongDetailPagerModel(
            songBookEntry = songBookEntry,
            getSongEntriesForSongbookUseCase = getSongEntriesForSongbookUseCase,
            getAvailableMediaForSongUseCase = getAvailableMediaForSongUseCase,
            getAvailableSheetMusicForSongUseCase = getAvailableSheetMusicForSongUseCase,
            changePreferenceUseCase = changePreferenceUseCase,
            getSongPreferenceFlowUseCase = getSongPreferenceFlowUseCase,
            changeFontSizeUseCase = changeFontSizeUseCase,
        )
    }
}
