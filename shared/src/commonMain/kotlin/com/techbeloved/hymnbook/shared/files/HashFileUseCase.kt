package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.model.file.FileHash
import kotlinx.coroutines.withContext
import okio.HashingSink
import okio.Path
import okio.blackholeSink
import okio.buffer
import okio.use

internal class HashFileUseCase(
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
    private val fileSystemProvider: OkioFileSystemProvider = defaultOkioFileSystemProvider,
) {

    suspend operator fun invoke(filePath: Path): FileHash = withContext(dispatchersProvider.io()) {
        val hash = HashingSink.sha256(blackholeSink()).use { hashingSink ->
            fileSystemProvider.get().fileSystem
                .source(filePath)
                .buffer().use { source ->
                    source.readAll(hashingSink)
                    hashingSink.hash.hex()
                }
        }
        FileHash(path = filePath.toString(), sha256 = hash)
    }
}
