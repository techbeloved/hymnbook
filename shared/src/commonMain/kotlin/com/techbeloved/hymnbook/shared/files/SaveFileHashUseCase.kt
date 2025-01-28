package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.model.file.FileHash
import com.techbeloved.hymnbook.shared.repository.FileRepository
import me.tatarka.inject.annotations.Inject

internal class SaveFileHashUseCase @Inject constructor(private val repository: FileRepository) {
    suspend operator fun invoke(fileHash: FileHash) = repository.saveAssetFileHash(fileHash)
}
