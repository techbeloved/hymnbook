package com.techbeloved.hymnbook.shared.files

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

internal actual val defaultAssetArchiveExtractor: AssetArchiveExtractor by lazy { IosArchiveExtractor() }

private class IosArchiveExtractor(
    private val systemArchiveExtractor: SystemArchiveExtractor = SystemArchiveExtractor(),
) : AssetArchiveExtractor {
    override suspend fun extract(assetFile: String, destination: Path) {
        systemArchiveExtractor.extract(
            sourceFile = getAssetFilePath(assetFile).toPath(),
            destination = destination,
            sourceFileSystem = FileSystem.SYSTEM,
            destinationFileSystem = FileSystem.SYSTEM
        )
    }
}
