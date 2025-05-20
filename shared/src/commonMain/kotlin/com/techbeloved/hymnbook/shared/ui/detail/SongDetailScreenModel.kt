package com.techbeloved.hymnbook.shared.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.model.SheetMusic
import com.techbeloved.hymnbook.shared.preferences.GetSongPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.sheetmusic.GetAvailableSheetMusicForSongUseCase
import com.techbeloved.hymnbook.shared.songs.GetSongDetailUseCase
import com.techbeloved.sheetmusic.SheetMusicItem
import com.techbeloved.sheetmusic.SheetMusicType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

internal class SongDetailScreenModel(
    private val songId: Long,
    private val getSongDetailUseCase: GetSongDetailUseCase,
    private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase,
    getSongPreferenceFlowUseCase: GetSongPreferenceFlowUseCase,
) : ViewModel() {

    val state =
        getSongDetailFlow().combine(getSongPreferenceFlowUseCase()) { detail, prefs ->
            detail.copy(
                songDisplayMode = prefs.songDisplayMode,
                fontSize = prefs.fontSize,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SongUiDetail(),
        )

    private fun getSongDetailFlow() = flow {
        val sheetMusic = getAvailableSheetMusicForSongUseCase(songId)
        val songDetail = getSongDetailUseCase(songId).copy(
            sheetMusic = if (sheetMusic != null) {
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
        )
        emit(songDetail)
    }

    class Factory @Inject constructor(
        private val getSongDetailUseCase: GetSongDetailUseCase,
        private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase,
        private val getSongPreferenceFlowUseCase: GetSongPreferenceFlowUseCase,
    ) {

        fun create(songId: Long): SongDetailScreenModel = SongDetailScreenModel(
            songId = songId,
            getSongDetailUseCase = getSongDetailUseCase,
            getAvailableSheetMusicForSongUseCase = getAvailableSheetMusicForSongUseCase,
            getSongPreferenceFlowUseCase = getSongPreferenceFlowUseCase,
        )
    }

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
