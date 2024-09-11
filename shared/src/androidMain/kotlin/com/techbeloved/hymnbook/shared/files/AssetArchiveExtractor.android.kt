package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import kotlinx.coroutines.withContext
import okio.Path
import java.util.zip.ZipInputStream

internal actual val defaultAssetArchiveExtractor: AssetArchiveExtractor by lazy { AndroidArchiveExtractor() }

private class AndroidArchiveExtractor(
    private val decompress: Decompress = Decompress(),
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
) : AssetArchiveExtractor {
    override suspend fun extract(assetFile: String, destination: Path) =
        withContext(dispatchersProvider.io()) {
            decompress.unzip(ZipInputStream(openAndroidAsset(assetFile)), destination.toFile())
        }
}
