package com.techbeloved.hymnbook.shared.files

import okio.Path

internal expect val defaultAssetArchiveExtractor: AssetArchiveExtractor

internal fun interface AssetArchiveExtractor {
    fun extract(assetFile: String, destination: Path)
}
