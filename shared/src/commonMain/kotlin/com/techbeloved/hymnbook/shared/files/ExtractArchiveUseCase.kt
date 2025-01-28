package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import okio.Path

internal class ExtractArchiveUseCase @Inject constructor(
    private val assetArchiveExtractor: AssetArchiveExtractor,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(assetFilePath: String, destination: Path): Result<Unit> =
        withContext(dispatchersProvider.io()) {
            runCatching {
                assetArchiveExtractor.extract(assetFilePath, destination)
            }
        }
}
