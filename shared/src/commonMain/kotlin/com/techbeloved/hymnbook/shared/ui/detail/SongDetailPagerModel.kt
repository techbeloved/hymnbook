package com.techbeloved.hymnbook.shared.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.analytics.TrackAnalyticsEventUseCase
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.media.GetAvailableMediaForSongUseCase
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.ChangeFontSizeUseCase
import com.techbeloved.hymnbook.shared.preferences.ChangePreferenceUseCase
import com.techbeloved.hymnbook.shared.preferences.GetPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.preferences.GetSongPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.preferences.SongPreferences
import com.techbeloved.hymnbook.shared.settings.DarkModePreference
import com.techbeloved.hymnbook.shared.settings.DarkModePreferenceKey
import com.techbeloved.hymnbook.shared.sheetmusic.GetAvailableSheetMusicForSongUseCase
import com.techbeloved.hymnbook.shared.songbooks.GetSongbookEntriesForSongUseCase
import com.techbeloved.hymnbook.shared.songs.GetSongIdsByFilterUseCase
import com.techbeloved.hymnbook.shared.songs.TrackSongViewUseCase
import com.techbeloved.hymnbook.shared.songshare.GetSongShareDataUseCase
import com.techbeloved.hymnbook.shared.soundfont.GetSoundFontPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.soundfont.IsSoundFontSupportedUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal class SongDetailPagerModel @Inject constructor(
    private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase,
    private val getSongbookEntriesForSongUseCase: GetSongbookEntriesForSongUseCase,
    private val getAvailableMediaForSongUseCase: GetAvailableMediaForSongUseCase,
    private val trackAnalyticsEventUseCase: TrackAnalyticsEventUseCase,
    private val getSongIdsByFilterUseCase: GetSongIdsByFilterUseCase,
    private val changePreferenceUseCase: ChangePreferenceUseCase,
    getSongPreferenceFlowUseCase: GetSongPreferenceFlowUseCase,
    private val changeFontSizeUseCase: ChangeFontSizeUseCase,
    private val getSoundFontPreferenceFlowUseCase: GetSoundFontPreferenceFlowUseCase,
    private val isSoundFontSupportedUseCase: IsSoundFontSupportedUseCase,
    private val getSongShareDataUseCase: GetSongShareDataUseCase,
    private val trackSongViewUseCase: TrackSongViewUseCase,
    getPreferenceFlowUseCase: GetPreferenceFlowUseCase,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bottomSheetVisible = MutableStateFlow(false)

    private val preferencesFlow = combine(
        getSongPreferenceFlowUseCase(),
        getPreferenceFlowUseCase(DarkModePreferenceKey).map { DarkModePreference.valueOf(it) }
    ) { songPrefs, darkModePref -> songPrefs to darkModePref }

    private val route = savedStateHandle.toRoute<SongDetailScreen>()
    private val initialSongId = route.initialSongId

    private val selectedPage = MutableStateFlow(-1)
    val state = combine(
        preferencesFlow,
        getSongEntriesFlow().map { songIds ->
            object {
                val initialPage = songIds.indexOfFirst { it == initialSongId }
                val songEntries = songIds
            }
        },
        selectedPage,
        getSoundFontState(),
        bottomSheetVisible,
    ) { (preferences, darkMode), songEntries, selectedPageIndex, soundFontState, bottomSheetVisible ->

        val currentIndex = if (selectedPageIndex < 0) songEntries.initialPage else selectedPageIndex
        val currentEntry = songEntries.songEntries[currentIndex]
        val songbookEntries = getSongbookEntriesForSongUseCase(currentEntry)
        val availableMedia = getAvailableMediaForSongUseCase(currentEntry)
        val availableSheetMusic = getAvailableSheetMusicForSongUseCase(currentEntry)
        val songShareData = getSongShareDataUseCase(currentEntry)

        SongDetailPagerState.Content(
            initialPage = songEntries.initialPage,
            pageCount = songEntries.songEntries.size,
            isSheetMusicAvailableForCurrentItem = availableSheetMusic != null,

            audioItem = availableMedia.firstOrNull { item ->
                if (preferences.isPreferMidi) item.isMidi() else true
            } ?: availableMedia.firstOrNull(),

            currentSongId = currentEntry,

            pages = songEntries.songEntries.toImmutableList(),
            currentDisplayMode = preferences.songDisplayMode,
            currentSongBookEntry = songbookEntries.firstOrNull(),
            soundFontState = soundFontState,
            shareAppData = songShareData,
            bottomSheetState = if (bottomSheetVisible) {
                DetailBottomSheetState.Show(
                    preferences = preferences,
                    darkModePreference = darkMode,
                )
            } else {
                DetailBottomSheetState.Hidden
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SongDetailPagerState.Loading,
    )

    fun onScreenLoaded() {
        viewModelScope.launch {
            trackAnalyticsEventUseCase(SongDetailAnalytics.screenView(route.songFilter))
        }
    }

    fun trackSongLoopingToggle(isLooping: Boolean) {
        viewModelScope.launch {
            trackAnalyticsEventUseCase(SongDetailAnalytics.actionToggleLooping(isLooping))
        }
    }

    fun trackSongSpeed(speed: Int) {
        viewModelScope.launch {
            trackAnalyticsEventUseCase(SongDetailAnalytics.actionChangeSpeed(speed))
        }
    }

    private fun getSongEntriesFlow() =
        flow { emit(getSongIdsByFilterUseCase(route.songFilter)) }

    fun onPageSelected(pageIndex: Int) {
        selectedPage.update { pageIndex }
    }

    fun onChangeSongDisplayMode(songDisplayMode: SongDisplayMode) {
        viewModelScope.launch {
            changePreferenceUseCase(SongPreferences.songDisplayModePrefKey) { songDisplayMode.name }
        }
    }

    fun onToggleDarkMode(darkModePreference: DarkModePreference) {
        viewModelScope.launch {
            changePreferenceUseCase(DarkModePreferenceKey) { darkModePreference.name }
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

    private fun getSoundFontState() =
        if (isSoundFontSupportedUseCase()) getSoundFontPreferenceFlowUseCase().map { soundFont ->
            if (soundFont != null) {
                SoundFontState.Available(soundFont)
            } else {
                SoundFontState.NotAvailable
            }
        } else {
            flowOf(SoundFontState.NotSupported)
        }

    fun onTrackCurrentSong(
        songId: Long,
        songbookEntry: SongBookEntry?,
    ) {
        viewModelScope.launch {
            trackSongViewUseCase(
                songId = songId,
                songbookEntry = songbookEntry,
            )
        }
    }

    @Inject
    class Factory(val create: (SavedStateHandle) -> SongDetailPagerModel)

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                appComponent.detailPagerScreenModelFactory().create(createSavedStateHandle())
            }
        }
    }
}
