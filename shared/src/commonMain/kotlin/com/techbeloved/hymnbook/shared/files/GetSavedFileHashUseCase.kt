package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.repository.FileRepository
import me.tatarka.inject.annotations.Inject

internal class GetSavedFileHashUseCase @Inject constructor(private val repository: FileRepository) {
    suspend operator fun invoke(assetFile: String) = repository.getAssetFileHash(assetFile)
}
