package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.model.file.FileHash
import com.techbeloved.hymnbook.shared.repository.FileRepository

internal class SaveFileHashUseCase(private val repository: FileRepository = FileRepository()) {
    suspend operator fun invoke(fileHash: FileHash) = repository.saveAssetFileHash(fileHash)
}
