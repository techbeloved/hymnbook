package com.techbeloved.hymnbook.shared.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.media.GetAvailableMediaForSongUseCase
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.ChangeFontSizeUseCase
import com.techbeloved.hymnbook.shared.preferences.ChangePreferenceUseCase
import com.techbeloved.hymnbook.shared.preferences.GetSongPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.preferences.SongPreferences
import com.techbeloved.hymnbook.shared.sheetmusic.GetAvailableSheetMusicForSongUseCase
import com.techbeloved.hymnbook.shared.songs.GetSongEntriesForSongbookUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

internal class SongDetailPagerModel(
    private val getSongEntriesForSongbookUseCase: GetSongEntriesForSongbookUseCase,
    private val getAvailableMediaForSongUseCase: GetAvailableMediaForSongUseCase,
    private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase,
    private val changePreferenceUseCase: ChangePreferenceUseCase,
    getSongPreferenceFlowUseCase: GetSongPreferenceFlowUseCase,
    private val changeFontSizeUseCase: ChangeFontSizeUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bottomSheetVisible = MutableStateFlow(false)

    val bottomSheetState =
        bottomSheetVisible.combine(getSongPreferenceFlowUseCase()) { visible, prefs ->
            if (visible) DetailBottomSheetState.Show(prefs) else DetailBottomSheetState.Hidden
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            DetailBottomSheetState.Hidden,
        )

    private val initialSongbookEntry = savedStateHandle.toRoute<SongDetailScreen>().let {
        SongBookEntry(
            songbook = it.songbook,
            entry = it.entry
        )
    }

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
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SongDetailPagerState.Loading,
    )

    private fun getSongEntriesFlow() =
        flow { emit(getSongEntriesForSongbookUseCase(initialSongbookEntry)) }

    fun onPageSelected(pageIndex: Int) {
        selectedPage.update { pageIndex }
    }

    fun onChangeSongDisplayMode(songDisplayMode: SongDisplayMode) {
        viewModelScope.launch {
            changePreferenceUseCase(SongPreferences.songDisplayModePrefKey) { songDisplayMode.name }
        }
    }

    fun onShowSettings() = bottomSheetVisible.update { true }

    fun onHideSettings() = bottomSheetVisible.update { false }

    fun onIncreaseFontSize() {
        viewModelScope.launch {
            changeFontSizeUseCase(isIncrease = true)
        }
    }

    fun onDecreaseFontSize() {
        viewModelScope.launch {
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
    ) : ViewModelProvider.Factory {

        fun create(
            savedStateHandle: SavedStateHandle,
        ): SongDetailPagerModel = SongDetailPagerModel(
            getSongEntriesForSongbookUseCase = getSongEntriesForSongbookUseCase,
            getAvailableMediaForSongUseCase = getAvailableMediaForSongUseCase,
            getAvailableSheetMusicForSongUseCase = getAvailableSheetMusicForSongUseCase,
            changePreferenceUseCase = changePreferenceUseCase,
            getSongPreferenceFlowUseCase = getSongPreferenceFlowUseCase,
            changeFontSizeUseCase = changeFontSizeUseCase,
            savedStateHandle = savedStateHandle,
        )
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                appComponent.detailPagerScreenModelFactory().create(createSavedStateHandle())
            }
        }
    }
}
