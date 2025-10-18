package com.techbeloved.hymnbook.shared.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.model.SheetMusic
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.model.ext.authors
import com.techbeloved.hymnbook.shared.model.ext.lyricsByVerseOrder
import com.techbeloved.hymnbook.shared.model.ext.lyricsCompact
import com.techbeloved.hymnbook.shared.model.ext.songbookEntries
import com.techbeloved.hymnbook.shared.model.ext.topics
import com.techbeloved.hymnbook.shared.preferences.GetSongPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.sheetmusic.GetAvailableSheetMusicForSongUseCase
import com.techbeloved.hymnbook.shared.songs.GetSongDetailUseCase
import com.techbeloved.hymnbook.shared.songs.SongData
import com.techbeloved.sheetmusic.SheetMusicItem
import com.techbeloved.sheetmusic.SheetMusicType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal class SongDetailScreenModel @Inject constructor(
    @Assisted private val songId: Long,
    private val getSongDetailUseCase: GetSongDetailUseCase,
    private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase,
    getSongPreferenceFlowUseCase: GetSongPreferenceFlowUseCase,
) : ViewModel() {

    private val songDetailFlow = flow { emit(getSongDetailUseCase(songId)) }
    private val sheetMusicFlow = flow { emit(getAvailableSheetMusicForSongUseCase(songId)) }
    private val songPreferencesFlow = getSongPreferenceFlowUseCase()


    val state = combine(
        songDetailFlow,
        sheetMusicFlow,
        songPreferencesFlow,
    ) { songDetail, sheetMusic, prefs ->
        SongUiDetail(
            sheetMusic = if (prefs.songDisplayMode == SongDisplayMode.SheetMusic && sheetMusic != null) {
                SheetMusicItem(
                    relativeUri = sheetMusic.relativePath.toString(),
                    type = when (sheetMusic.type) {
                        SheetMusic.Type.Pdf -> SheetMusicType.Pdf
                        SheetMusic.Type.Image -> SheetMusicType.Image
                    },
                    absoluteUri = sheetMusic.absolutePath.toString(),
                )
            } else {
                null
            },
            content = SongData(
                title = songDetail.title,
                alternativeTitles = songDetail.alternate_title?.let { listOf(it) } ?: emptyList(),
                authors = songDetail.authors(),
                lyrics = when(prefs.songDisplayMode) {
                    SongDisplayMode.Lyrics ->  songDetail.lyricsByVerseOrder()
                    SongDisplayMode.LyricsCompact -> songDetail.lyricsCompact()
                    SongDisplayMode.SheetMusic -> emptyList()
                },
                songbookEntries = songDetail.songbookEntries(),
                topics = songDetail.topics(),
            ),
            songDisplayMode = prefs.songDisplayMode,
            fontSizeMultiplier = prefs.fontSize,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SongUiDetail(),
    )

    @Inject
    class Factory(val create: (songId: Long) -> SongDetailScreenModel)

    companion object {
        val SONG_ID_KEY = object : CreationExtras.Key<Long> {}
        val Factory = viewModelFactory {

            initializer {
                val songId = this[SONG_ID_KEY] as Long
                appComponent.detailScreenModelFactory().create(songId)
            }
        }
    }
}
