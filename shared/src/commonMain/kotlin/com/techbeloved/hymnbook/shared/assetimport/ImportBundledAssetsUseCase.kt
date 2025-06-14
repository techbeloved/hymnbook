package com.techbeloved.hymnbook.shared.assetimport

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.ext.sheetsDir
import com.techbeloved.hymnbook.shared.ext.tunesDir
import com.techbeloved.hymnbook.shared.files.AssetFileSourceProvider
import com.techbeloved.hymnbook.shared.files.ExtractArchiveUseCase
import com.techbeloved.hymnbook.shared.files.GetSavedFileHashUseCase
import com.techbeloved.hymnbook.shared.files.HashAssetFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.SaveFileHashUseCase
import com.techbeloved.hymnbook.shared.files.SharedFileSystem
import com.techbeloved.hymnbook.shared.jsonimport.ImportJsonSongUseCase
import com.techbeloved.hymnbook.shared.media.ImportMediaFilesUseCase
import com.techbeloved.hymnbook.shared.model.assetimport.AssetType
import com.techbeloved.hymnbook.shared.model.assetimport.BundledAsset
import com.techbeloved.hymnbook.shared.model.assetimport.BundledAssetManifest
import com.techbeloved.hymnbook.shared.openlyrics.ImportOpenLyricsUseCase
import com.techbeloved.hymnbook.shared.sheetmusic.ImportMusicSheetsUseCase
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import okio.buffer
import okio.use

internal class ImportBundledAssetsUseCase @Inject constructor(
    private val hashAssetFileUseCase: HashAssetFileUseCase,
    private val extractArchiveUseCase: ExtractArchiveUseCase,
    private val importOpenLyricsUseCase: ImportOpenLyricsUseCase,
    private val importJsonSongUseCase: ImportJsonSongUseCase,
    private val fileSystemProvider: OkioFileSystemProvider,
    private val getSavedFileHashUseCase: GetSavedFileHashUseCase,
    private val saveFileHashUseCase: SaveFileHashUseCase,
    private val importMediaFilesUseCase: ImportMediaFilesUseCase,
    private val importMusicSheetsUseCase: ImportMusicSheetsUseCase,
    private val defaultAssetFileSourceProvider: AssetFileSourceProvider,
    private val dispatchersProvider: DispatchersProvider,
    private val json: Json,
) {
    suspend operator fun invoke() = withContext(dispatchersProvider.io()) {
        val fileSystem = fileSystemProvider.get()
        val manifestJson = defaultAssetFileSourceProvider.get("files/manifest/filesmanifest.json")
            .use { fileSource ->
                fileSource.buffer().use { bufferedSource ->
                    bufferedSource.readUtf8()
                }
            }
        val bundledAssets = json.decodeFromString(
            deserializer = BundledAssetManifest.serializer(),
            string = manifestJson,
        )
        importOpenLyrics(fileSystem, bundledAssets.openlyrics)
        importJsonHymnbook(bundledAssets.json)
        importTunes(fileSystem, bundledAssets.tunes)
        importSheets(fileSystem, bundledAssets.sheets)
    }

    private suspend fun importTunes(
        fileSystem: SharedFileSystem,
        tunesAssets: List<BundledAsset>,
    ) {
        tunesAssets.filter { it.type == AssetType.ZIP }.forEach { bundledAsset ->
            val tunesAssetFileHash = hashAssetFileUseCase(bundledAsset.fullPath)
            val savedTunesArchiveHash = getSavedFileHashUseCase(bundledAsset.fullPath)
            if (savedTunesArchiveHash?.sha256 != tunesAssetFileHash.sha256) {
                val tunesDir = fileSystem.tunesDir()
                if (!fileSystem.fileSystem.exists(tunesDir)) {
                    fileSystem.fileSystem.createDirectory(tunesDir)
                }
                val result = extractArchiveUseCase(
                    assetFilePath = bundledAsset.fullPath,
                    destination = tunesDir,
                ).onFailure { it.printStackTrace() }
                if (result.isSuccess) {
                    importMediaFilesUseCase(tunesDir).onFailure { it.printStackTrace() }
                    saveFileHashUseCase(tunesAssetFileHash)
                }
            }
        }
    }

    private suspend fun importSheets(
        fileSystem: SharedFileSystem,
        sheetsAssets: List<BundledAsset>,
    ) {
        sheetsAssets.filter { it.type == AssetType.ZIP }.forEach { bundledAsset ->
            val sheetsAssetFileHash = hashAssetFileUseCase(bundledAsset.fullPath)
            val savedSheetsArchiveHash = getSavedFileHashUseCase(bundledAsset.fullPath)

            if (savedSheetsArchiveHash?.sha256 != sheetsAssetFileHash.sha256) {
                val sheetsDir = fileSystem.sheetsDir()
                if (!fileSystem.fileSystem.exists(sheetsDir)) {
                    fileSystem.fileSystem.createDirectory(sheetsDir)
                }
                val result = extractArchiveUseCase(
                    assetFilePath = bundledAsset.fullPath,
                    destination = sheetsDir
                ).onFailure { it.printStackTrace() }
                if (result.isSuccess) {
                    importMusicSheetsUseCase(sheetsDir).onFailure { it.printStackTrace() }
                    saveFileHashUseCase(sheetsAssetFileHash)
                }
            }
        }

    }

    private suspend fun importJsonHymnbook(
        jsonAssets: List<BundledAsset>,
    ) {
        jsonAssets.filter { it.type == AssetType.JSON }.forEach { bundledAsset ->
            val lyricsAssetFileHash = hashAssetFileUseCase(bundledAsset.fullPath)
            val savedLyricsArchiveHash = getSavedFileHashUseCase(bundledAsset.fullPath)
            if (savedLyricsArchiveHash?.sha256 != lyricsAssetFileHash.sha256) {
                importJsonSongUseCase(bundledAsset.fullPath)
                saveFileHashUseCase(lyricsAssetFileHash)
            }
        }
    }

    private suspend fun importOpenLyrics(
        fileSystem: SharedFileSystem,
        openLyricsAssets: List<BundledAsset>,
    ) {
        openLyricsAssets.filter { it.type == AssetType.ZIP }.forEach { bundledAsset ->
            val lyricsAssetFileHash = hashAssetFileUseCase(bundledAsset.fullPath)
            val savedLyricsArchiveHash = getSavedFileHashUseCase(bundledAsset.fullPath)
            if (savedLyricsArchiveHash?.sha256 != lyricsAssetFileHash.sha256) {
                val lyricsDir = fileSystem.tempDir / "lyrics/"
                if (!fileSystem.fileSystem.exists(lyricsDir)) {
                    fileSystem.fileSystem.createDirectory(lyricsDir)
                }
                val result = extractArchiveUseCase(
                    assetFilePath = bundledAsset.fullPath,
                    destination = lyricsDir,
                )
                if (result.isSuccess) {
                    importOpenLyricsUseCase(lyricsDir)
                    saveFileHashUseCase(lyricsAssetFileHash)
                }
                // Delete temporary files
                fileSystem.fileSystem.deleteRecursively(lyricsDir)

            }
        }
    }
}
