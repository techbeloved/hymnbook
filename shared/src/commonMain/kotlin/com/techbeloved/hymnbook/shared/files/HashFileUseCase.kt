package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.file.FileHash
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import okio.HashingSink
import okio.Path
import okio.blackholeSink
import okio.buffer
import okio.use

internal class HashFileUseCase @Inject constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val fileSystemProvider: OkioFileSystemProvider,
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
