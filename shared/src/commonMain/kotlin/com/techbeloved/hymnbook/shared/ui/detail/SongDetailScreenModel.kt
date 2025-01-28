package com.techbeloved.hymnbook.shared.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
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
) : ScreenModel {

    val state =
        getSongDetailFlow().combine(getSongPreferenceFlowUseCase()) { detail, prefs ->
            detail.copy(
                songDisplayMode = prefs.songDisplayMode,
                fontSize = prefs.fontSize,
            )
        }.stateIn(
            scope = screenModelScope,
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
}
