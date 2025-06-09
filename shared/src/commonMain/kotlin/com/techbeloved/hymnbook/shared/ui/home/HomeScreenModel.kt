package com.techbeloved.hymnbook.shared.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.SongbookEntity
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.ext.sheetsDir
import com.techbeloved.hymnbook.shared.ext.tunesDir
import com.techbeloved.hymnbook.shared.files.ExtractArchiveUseCase
import com.techbeloved.hymnbook.shared.files.GetSavedFileHashUseCase
import com.techbeloved.hymnbook.shared.files.HashAssetFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.SaveFileHashUseCase
import com.techbeloved.hymnbook.shared.files.SharedFileSystem
import com.techbeloved.hymnbook.shared.media.ImportMediaFilesUseCase
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.openlyrics.ImportOpenLyricsUseCase
import com.techbeloved.hymnbook.shared.sheetmusic.ImportMusicSheetsUseCase
import com.techbeloved.hymnbook.shared.songbooks.GetAllSongbooksUseCase
import com.techbeloved.hymnbook.shared.titles.GetFilteredSongTitlesUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

internal class HomeScreenModel @Inject constructor(
    private val hashAssetFileUseCase: HashAssetFileUseCase,
    private val extractArchiveUseCase: ExtractArchiveUseCase,
    private val importOpenLyricsUseCase: ImportOpenLyricsUseCase,
    private val getFilteredSongTitlesUseCase: GetFilteredSongTitlesUseCase,
    private val fileSystemProvider: OkioFileSystemProvider,
    private val getSavedFileHashUseCase: GetSavedFileHashUseCase,
    private val saveFileHashUseCase: SaveFileHashUseCase,
    private val importMediaFilesUseCase: ImportMediaFilesUseCase,
    private val importMusicSheetsUseCase: ImportMusicSheetsUseCase,
    private val getAllSongbooksUseCase: GetAllSongbooksUseCase,
) : ViewModel() {

    private val assetsReady = MutableStateFlow(false)
    private val sortBy = MutableStateFlow(value = SortBy.Number)
    private val selectedSongbook = MutableStateFlow<SongbookEntity?>(value = null)
    private val songbooks = getAllSongbooksUseCase().map { it.toImmutableList() }

    val state = combine(
        assetsReady,
        sortBy,
        selectedSongbook,
        songbooks,
    ) { assetsReady, sortBy, selectedSongbook, songbooks ->
        if (assetsReady) {
            val songbook = selectedSongbook ?: songbooks.firstOrNull()
            HomeScreenState(
                songTitles = getFilteredSongTitlesUseCase(
                    songFilter = SongFilter.songbookFilter(
                        songbook = songbook?.name.orEmpty(),
                        sortByTitle = sortBy == SortBy.Title,
                    )
                ).toImmutableList(),
                songbooks = songbooks,
                currentSongbook = selectedSongbook ?: songbook,
                isLoading = false,
                sortBy = sortBy,
            )
        } else {
            HomeScreenState.EmptyLoading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeScreenState.EmptyLoading,
    )

    init {
        viewModelScope.launch {
            importBundledAssets()
            assetsReady.update { true }
        }
    }

    fun onUpdateSortBy(sortBy: SortBy) {
        this.sortBy.update { sortBy }
    }

    fun onUpdateSongbook(songbook: SongbookEntity) {
        selectedSongbook.update { songbook }
    }

    private suspend fun importBundledAssets() {
        val fileSystem = fileSystemProvider.get()

        // Lyrics assets
        importBundledLyrics(fileSystem)

        importBundledTunes(fileSystem)

        importBundledSheets(fileSystem)
    }

    private suspend fun importBundledLyrics(fileSystem: SharedFileSystem) {
        val lyricsBundledAsset =
            "files/openlyrics/sample_songs.zip" // update the name with the final name
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

    private suspend fun importBundledTunes(fileSystem: SharedFileSystem) {
        val tunesBundledAsset =
            "files/tunes/sample_tunes.zip" // update the name with the final name
        val tunesAssetFileHash = hashAssetFileUseCase(tunesBundledAsset)
        val savedTunesArchiveHash = getSavedFileHashUseCase(tunesBundledAsset)

        if (savedTunesArchiveHash?.sha256 != tunesAssetFileHash.sha256) {
            val tunesDir = fileSystem.tunesDir()
            fileSystem.fileSystem.createDirectory(tunesDir)

            val result = extractArchiveUseCase(
                assetFilePath = tunesBundledAsset,
                destination = tunesDir
            ).onFailure { it.printStackTrace() }

            if (result.isSuccess) {
                importMediaFilesUseCase(tunesDir).onFailure { it.printStackTrace() }
                saveFileHashUseCase(tunesAssetFileHash)
            }
        }
    }

    private suspend fun importBundledSheets(fileSystem: SharedFileSystem) {
        val sheetsBundledAsset =
            "files/sheets/sample_sheets.zip" // update the name with the final name
        val sheetsAssetFileHash = hashAssetFileUseCase(sheetsBundledAsset)
        val savedTunesArchiveHash = getSavedFileHashUseCase(sheetsBundledAsset)

        if (savedTunesArchiveHash?.sha256 != sheetsAssetFileHash.sha256) {
            val sheetsDir = fileSystem.sheetsDir()
            fileSystem.fileSystem.createDirectory(sheetsDir)

            val result = extractArchiveUseCase(
                assetFilePath = sheetsBundledAsset,
                destination = sheetsDir
            ).onFailure { it.printStackTrace() }

            if (result.isSuccess) {
                importMusicSheetsUseCase(sheetsDir).onFailure { it.printStackTrace() }
                saveFileHashUseCase(sheetsAssetFileHash)
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { appComponent.homeScreenModel() }
        }
    }
}
