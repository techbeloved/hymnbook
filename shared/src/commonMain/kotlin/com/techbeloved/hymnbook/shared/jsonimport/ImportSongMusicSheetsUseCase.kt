package com.techbeloved.hymnbook.shared.jsonimport

import com.techbeloved.hymnbook.shared.ext.sheetsDir
import com.techbeloved.hymnbook.shared.files.ExtractArchiveUseCase
import com.techbeloved.hymnbook.shared.files.GetSavedFileHashUseCase
import com.techbeloved.hymnbook.shared.files.HashAssetFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.SaveFileHashUseCase
import com.techbeloved.hymnbook.shared.model.assetimport.BundledAsset
import com.techbeloved.hymnbook.shared.sheetmusic.ImportMusicSheetsUseCase
import me.tatarka.inject.annotations.Inject

internal class ImportSongMusicSheetsUseCase @Inject constructor(
    private val hashAssetFileUseCase: HashAssetFileUseCase,
    private val extractArchiveUseCase: ExtractArchiveUseCase,
    private val getSavedFileHashUseCase: GetSavedFileHashUseCase,
    private val importMusicSheetsUseCase: ImportMusicSheetsUseCase,
    private val saveFileHashUseCase: SaveFileHashUseCase,
    private val fileSystemProvider: OkioFileSystemProvider,
) {
    suspend operator fun invoke(
        sheetMusicArchiveAsset: BundledAsset,
        prefix: String,
        songbook: String
    ) {
        val fileSystem = fileSystemProvider.get()
        val sheetsAssetFileHash = hashAssetFileUseCase(sheetMusicArchiveAsset.fullPath)
        val savedSheetsArchiveHash = getSavedFileHashUseCase(sheetMusicArchiveAsset.fullPath)

        if (savedSheetsArchiveHash?.sha256 != sheetsAssetFileHash.sha256) {
            val sheetsDir = fileSystem.sheetsDir()
            if (!fileSystem.fileSystem.exists(sheetsDir)) {
                fileSystem.fileSystem.createDirectory(sheetsDir)
            }
            val result = extractArchiveUseCase(
                assetFilePath = sheetMusicArchiveAsset.fullPath,
                destination = sheetsDir
            ).onFailure { it.printStackTrace() }
            if (result.isSuccess) {
                importMusicSheetsUseCase(
                    directory = sheetsDir,
                    prefix = prefix,
                    songbook = songbook
                ).onFailure { it.printStackTrace() }
                saveFileHashUseCase(sheetsAssetFileHash)
            }
        }
    }
}
