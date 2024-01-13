package com.techbeloved.hymnbook.shared.files

import okio.Path
import java.util.zip.ZipInputStream

internal actual val defaultAssetArchiveExtractor: AssetArchiveExtractor by lazy { AndroidArchiveExtractor() }

private class AndroidArchiveExtractor(
    private val decompress: Decompress = Decompress(),
) : AssetArchiveExtractor {
    override fun extract(assetFile: String, destination: Path) {
        decompress.unzip(ZipInputStream(openAndroidAsset(assetFile)), destination.toFile())
    }
}
