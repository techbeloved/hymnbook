package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import kotlinx.coroutines.withContext
import okio.Path

internal class ExtractArchiveUseCase(
    private val assetArchiveExtractor: AssetArchiveExtractor = defaultAssetArchiveExtractor,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
) {
    suspend operator fun invoke(assetFilePath: String, destination: Path): Result<Unit> =
        withContext(dispatchersProvider.io()) {
            runCatching {
                assetArchiveExtractor.extract(assetFilePath, destination)
            }
        }
}
