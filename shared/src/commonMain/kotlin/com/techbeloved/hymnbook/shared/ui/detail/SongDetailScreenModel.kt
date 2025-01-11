package com.techbeloved.hymnbook.shared.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.model.SheetMusic
import com.techbeloved.hymnbook.shared.preferences.PreferencesRepository
import com.techbeloved.hymnbook.shared.sheetmusic.GetAvailableSheetMusicForSongUseCase
import com.techbeloved.sheetmusic.SheetMusicItem
import com.techbeloved.sheetmusic.SheetMusicType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

internal class SongDetailScreenModel(
    private val songId: Long,
    private val getSongDetailUseCase: GetSongDetailUseCase = GetSongDetailUseCase(),
    private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase = GetAvailableSheetMusicForSongUseCase(),
    preferencesRepository: PreferencesRepository = Injector.preferencesRepository,
) : ScreenModel {

    val state = getSongDetailFlow().combine(preferencesRepository.songPreferences) { detail, prefs ->
        detail.copy(songDisplayMode = prefs.songDisplayMode)
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
}
