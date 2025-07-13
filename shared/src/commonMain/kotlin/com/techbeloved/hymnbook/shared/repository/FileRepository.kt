package com.techbeloved.hymnbook.shared.repository

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.file.FileHash
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class FileRepository @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {

    suspend fun getAssetFileHash(filePath: String): FileHash? =
        withContext(dispatchersProvider.io()) {
            database.bundledAssetEntityQueries.getByFilePath(filePath) { path, hash ->
                FileHash(path, hash.orEmpty())
            }.executeAsOneOrNull()
        }

    suspend fun saveAssetFileHash(fileHash: FileHash) = withContext(dispatchersProvider.io()) {
        database.bundledAssetEntityQueries.insert(fileHash.path, fileHash.sha256).await()
    }
}
