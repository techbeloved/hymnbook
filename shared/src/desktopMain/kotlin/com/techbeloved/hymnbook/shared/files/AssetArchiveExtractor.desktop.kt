package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.asResourceFileSystem
import okio.buffer
import java.util.zip.ZipInputStream

internal actual val defaultAssetArchiveExtractor: AssetArchiveExtractor by lazy {
    DesktopAssetArchiveExtractor()
}

private class DesktopAssetArchiveExtractor(
    private val decompress: Decompress = Decompress(),
    private val systemArchiveExtractor: SystemArchiveExtractor = SystemArchiveExtractor(
        DispatchersProvider()
    ),
) : AssetArchiveExtractor {
    override suspend fun extract(assetFile: String, destination: Path) {
        runCatching {
            systemArchiveExtractor.extract(
                sourceFile = getResourcePath(assetFile).toPath(),
                destination = destination,
                sourceFileSystem = FileSystem.RESOURCES,
                destinationFileSystem = FileSystem.SYSTEM
            )
        }.onFailure {
            withContext(Dispatchers.IO) {
                val classLoader = Thread.currentThread().contextClassLoader
                    ?: (::DesktopAssetArchiveExtractor.javaClass.classLoader)
                val assetStream = classLoader.asResourceFileSystem()
                    .source(getResourcePath(assetFile).toPath())
                    .buffer()
                    .inputStream()
                decompress.unzip(ZipInputStream(assetStream), destination.toFile())
            }
        }
    }
}
