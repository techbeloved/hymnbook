package com.techbeloved.hymnbook.shared.files

import cocoapods.SSZipArchive.SSZipArchive
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path

internal actual val defaultAssetArchiveExtractor: AssetArchiveExtractor by lazy { IosArchiveExtractor() }

@OptIn(ExperimentalForeignApi::class)
private class IosArchiveExtractor : AssetArchiveExtractor {
    override fun extract(assetFile: String, destination: Path) {
        val extracted = SSZipArchive.unzipFileAtPath(getAssetFilePath(assetFile), destination.toString())
        println("File extracted to $destination: $extracted")
    }
}
