package com.techbeloved.hymnbook.shared.files

import okio.Path

internal expect val defaultAssetArchiveExtractor: AssetArchiveExtractor

internal fun interface AssetArchiveExtractor {
    suspend fun extract(assetFile: String, destination: Path)
}
