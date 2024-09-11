package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import okio.use

/**
 * Extracts files from a zip archive.
 * This excludes android assets.
 * Regular archives in regular filesystems are supported for all platforms
 * [Credits](https://www.reddit.com/r/Kotlin/comments/ut2n62/comment/ltdx229/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button)
 */
internal class SystemArchiveExtractor(
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
) {
    suspend fun extract(
        sourceFile: Path,
        destination: Path,
        sourceFileSystem: FileSystem,
        destinationFileSystem: FileSystem,
    ) = withContext(dispatchersProvider.io()) {
        val zipFileSystem = sourceFileSystem.openZip(sourceFile)
        val paths = zipFileSystem.listRecursively("/".toPath())
            .filter { zipFileSystem.metadata(it).isRegularFile }

        paths.forEach { zipFilePath ->
            zipFileSystem.source(zipFilePath).buffer().use { source ->
                val relativeFilePath = zipFilePath.toString().trimStart('/')
                val fileToWrite = destination.resolve(relativeFilePath)
                // create parent directories
                fileToWrite.parent?.let { parent -> destinationFileSystem.createDirectories(parent) }
                destinationFileSystem.sink(fileToWrite).buffer()
                    .use { sink -> sink.writeAll(source) }
            }
        }
    }
}
