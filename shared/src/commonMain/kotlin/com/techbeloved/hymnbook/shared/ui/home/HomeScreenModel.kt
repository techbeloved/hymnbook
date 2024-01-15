package com.techbeloved.hymnbook.shared.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.files.ExtractArchiveUseCase
import com.techbeloved.hymnbook.shared.files.HashAssetFileUseCase
import com.techbeloved.hymnbook.shared.files.defaultOkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.HymnItem
import com.techbeloved.hymnbook.shared.openlyrics.ImportOpenLyricsUseCase
import com.techbeloved.hymnbook.shared.titles.GetHymnTitlesUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class HomeScreenModel(
    private val hashAssetFileUseCase: HashAssetFileUseCase = HashAssetFileUseCase(),
    private val extractArchiveUseCase: ExtractArchiveUseCase = ExtractArchiveUseCase(),
    private val importOpenLyricsUseCase: ImportOpenLyricsUseCase = ImportOpenLyricsUseCase(),
    private val getHymnTitlesUseCase: GetHymnTitlesUseCase = GetHymnTitlesUseCase(),
) : ScreenModel {
    val state: MutableStateFlow<ImmutableList<HymnItem>> = MutableStateFlow(persistentListOf())

    init {
        screenModelScope.launch {

            val assetFileHash = hashAssetFileUseCase("assets/openlyrics/ten_thousand_reason.xml")
            // TODO: Check if file has been imported before
            val fileSystem = defaultOkioFileSystemProvider.get()
            val lyricsDir =  fileSystem.tempDir / "lyrics/"
            fileSystem.fileSystem.createDirectory(lyricsDir)

            val result = extractArchiveUseCase(assetFilePath = "assets/openlyrics/sample_songs.zip", destination = lyricsDir)
            if (result.isSuccess) {
                importOpenLyricsUseCase(lyricsDir)
            } else {
                result.exceptionOrNull()?.printStackTrace()
            }
            state.value = getHymnTitlesUseCase()
        }
    }
}

