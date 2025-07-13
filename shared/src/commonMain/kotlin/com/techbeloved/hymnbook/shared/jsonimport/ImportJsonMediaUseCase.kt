package com.techbeloved.hymnbook.shared.jsonimport

import com.techbeloved.hymnbook.shared.ext.tunesDir
import com.techbeloved.hymnbook.shared.files.ExtractArchiveUseCase
import com.techbeloved.hymnbook.shared.files.GetSavedFileHashUseCase
import com.techbeloved.hymnbook.shared.files.HashAssetFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.SaveFileHashUseCase
import com.techbeloved.hymnbook.shared.media.ImportMediaFilesUseCase
import com.techbeloved.hymnbook.shared.model.assetimport.BundledAsset
import me.tatarka.inject.annotations.Inject

internal class ImportJsonMediaUseCase @Inject constructor(
    private val hashAssetFileUseCase: HashAssetFileUseCase,
    private val extractArchiveUseCase: ExtractArchiveUseCase,
    private val getSavedFileHashUseCase: GetSavedFileHashUseCase,
    private val saveFileHashUseCase: SaveFileHashUseCase,
    private val fileSystemProvider: OkioFileSystemProvider,
    private val importMediaFilesUseCase: ImportMediaFilesUseCase,
) {

    suspend operator fun invoke(jsonAssetFile: BundledAsset, prefix: String, songbook: String) {
        val fileSystem = fileSystemProvider.get()
        val tunesAssetFileHash = hashAssetFileUseCase(jsonAssetFile.fullPath)
        val savedTunesArchiveHash = getSavedFileHashUseCase(jsonAssetFile.fullPath)
        if (savedTunesArchiveHash?.sha256 != tunesAssetFileHash.sha256) {
            val tunesDir = fileSystem.tunesDir()
            if (!fileSystem.fileSystem.exists(tunesDir)) {
                fileSystem.fileSystem.createDirectory(tunesDir)
            }
            val result = extractArchiveUseCase(
                assetFilePath = jsonAssetFile.fullPath,
                destination = tunesDir,
            ).onFailure { it.printStackTrace() }
            if (result.isSuccess) {
                importMediaFilesUseCase(
                    directory = tunesDir,
                    prefix = prefix,
                    songbook = songbook,
                ).onFailure { it.printStackTrace() }
                saveFileHashUseCase(tunesAssetFileHash)
            }
        }
    }
}
