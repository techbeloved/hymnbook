package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.repository.FileRepository

internal class GetSavedFileHashUseCase(private val repository: FileRepository = FileRepository()) {
    suspend operator fun invoke(assetFile: String) = repository.getAssetFileHash(assetFile)
}
