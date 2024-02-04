package com.techbeloved.hymnbook.shared.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class SongDetailScreenModel(
    private val songId: Long,
    private val getSongDetailUseCase: GetSongDetailUseCase = GetSongDetailUseCase(),
) : ScreenModel {

    val state: MutableStateFlow<SongUiDetail> = MutableStateFlow(SongUiDetail())

    init {
        screenModelScope.launch {
            state.value = getSongDetailUseCase(songId)
        }
    }
}
