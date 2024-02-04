package com.techbeloved.hymnbook.shared.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.files.ExtractArchiveUseCase
import com.techbeloved.hymnbook.shared.files.GetSavedFileHashUseCase
import com.techbeloved.hymnbook.shared.files.HashAssetFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.SaveFileHashUseCase
import com.techbeloved.hymnbook.shared.files.defaultOkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.openlyrics.ImportOpenLyricsUseCase
import com.techbeloved.hymnbook.shared.titles.GetHymnTitlesUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class HomeScreenModel(
    private val hashAssetFileUseCase: HashAssetFileUseCase = HashAssetFileUseCase(),
    private val extractArchiveUseCase: ExtractArchiveUseCase = ExtractArchiveUseCase(),
    private val importOpenLyricsUseCase: ImportOpenLyricsUseCase = ImportOpenLyricsUseCase(),
    private val getHymnTitlesUseCase: GetHymnTitlesUseCase = GetHymnTitlesUseCase(),
    private val fileSystemProvider: OkioFileSystemProvider = defaultOkioFileSystemProvider,
    private val getSavedFileHashUseCase: GetSavedFileHashUseCase = GetSavedFileHashUseCase(),
    private val saveFileHashUseCase: SaveFileHashUseCase = SaveFileHashUseCase(),
) : ScreenModel {
    val state: MutableStateFlow<ImmutableList<SongTitle>> = MutableStateFlow(persistentListOf())

    init {
        screenModelScope.launch {

            importBundledAssets()
            state.value = getHymnTitlesUseCase().toImmutableList()
        }
    }

    private suspend fun importBundledAssets() {
        val fileSystem = fileSystemProvider.get()

        // Lyrics assets
        val lyricsBundledAsset = "assets/openlyrics/sample_songs.zip"
        val lyricsAssetFileHash = hashAssetFileUseCase(lyricsBundledAsset)
        val savedLyricsArchiveHash = getSavedFileHashUseCase(lyricsBundledAsset)

        // Check if file has been imported already. Otherwise, we ignore
        if (savedLyricsArchiveHash?.sha256 != lyricsAssetFileHash.sha256) {
            val lyricsDir = fileSystem.tempDir / "lyrics/"
            fileSystem.fileSystem.createDirectory(lyricsDir)

            val result = extractArchiveUseCase(
                assetFilePath = lyricsBundledAsset,
                destination = lyricsDir
            )
            if (result.isSuccess) {
                importOpenLyricsUseCase(lyricsDir)
                saveFileHashUseCase(lyricsAssetFileHash)
                // Delete temporary files
                fileSystem.fileSystem.deleteRecursively(lyricsDir)
            } else {
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}

