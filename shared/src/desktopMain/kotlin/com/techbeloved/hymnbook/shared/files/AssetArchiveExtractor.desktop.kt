package com.techbeloved.hymnbook.shared.files

import okio.FileNotFoundException
import okio.Path
import java.util.zip.ZipInputStream

internal actual val defaultAssetArchiveExtractor: AssetArchiveExtractor by lazy {
    DesktopAssetArchiveExtractor()
}

private class DesktopAssetArchiveExtractor(
    private val decompress: Decompress = Decompress(),
) : AssetArchiveExtractor {
    override fun extract(assetFile: String, destination: Path) {
        val classLoader = Thread.currentThread().contextClassLoader
            ?: (::DesktopAssetArchiveExtractor.javaClass.classLoader)
        val assetStream = classLoader.getResourceAsStream(assetFile)
        if (assetStream != null) {
            decompress.unzip(ZipInputStream(assetStream), destination.toFile())
        } else {
            throw FileNotFoundException("Failed to open asset")
        }
    }
}
