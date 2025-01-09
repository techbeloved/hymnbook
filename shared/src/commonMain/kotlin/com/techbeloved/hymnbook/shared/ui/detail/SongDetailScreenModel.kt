package com.techbeloved.hymnbook.shared.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.model.SheetMusic
import com.techbeloved.hymnbook.shared.sheetmusic.GetAvailableSheetMusicForSongUseCase
import com.techbeloved.sheetmusic.SheetMusicItem
import com.techbeloved.sheetmusic.SheetMusicType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class SongDetailScreenModel(
    private val songId: Long,
    private val getSongDetailUseCase: GetSongDetailUseCase = GetSongDetailUseCase(),
    private val getAvailableSheetMusicForSongUseCase: GetAvailableSheetMusicForSongUseCase = GetAvailableSheetMusicForSongUseCase(),
) : ScreenModel {

    val state: MutableStateFlow<SongUiDetail> = MutableStateFlow(SongUiDetail())
    private val preferredSheetMusic = MutableStateFlow(true) // FIXME: use in-memory settings

    init {
        screenModelScope.launch {
            val sheetMusic = getAvailableSheetMusicForSongUseCase(songId)
            println(sheetMusic)
            state.value = getSongDetailUseCase(songId).copy(
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
                preferSheetMusic = preferredSheetMusic.value,
            )
        }
    }
}
