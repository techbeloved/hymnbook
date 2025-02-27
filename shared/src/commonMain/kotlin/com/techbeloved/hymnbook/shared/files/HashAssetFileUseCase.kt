package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.file.FileHash
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import okio.HashingSink
import okio.blackholeSink
import okio.buffer
import okio.use

internal class HashAssetFileUseCase @Inject constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val defaultAssetFileSourceProvider: AssetFileSourceProvider,
) {
    suspend operator fun invoke(filePath: String): FileHash =
        withContext(dispatchersProvider.io()) {
            val hash = HashingSink.sha256(blackholeSink()).use { hashingSink ->
                defaultAssetFileSourceProvider.get(filePath).buffer().use { source ->
                    source.readAll(hashingSink)
                    hashingSink.hash.hex()
                }
            }
            FileHash(path = filePath, sha256 = hash)
        }
}
