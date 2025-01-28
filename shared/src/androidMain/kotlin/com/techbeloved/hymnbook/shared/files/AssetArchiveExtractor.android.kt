package com.techbeloved.hymnbook.shared.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Path
import java.util.zip.ZipInputStream

internal actual val defaultAssetArchiveExtractor: AssetArchiveExtractor by lazy { AndroidArchiveExtractor() }

private class AndroidArchiveExtractor(
    private val decompress: Decompress = Decompress(),
) : AssetArchiveExtractor {
    override suspend fun extract(assetFile: String, destination: Path) =
        withContext(Dispatchers.IO) {
            decompress.unzip(ZipInputStream(openAndroidAsset(assetFile)), destination.toFile())
        }
}
