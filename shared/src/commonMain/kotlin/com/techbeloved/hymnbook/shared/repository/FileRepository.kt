package com.techbeloved.hymnbook.shared.repository

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.model.file.FileHash
import kotlinx.coroutines.withContext

internal class FileRepository(
    private val database: Database = Injector.database,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
) {

    suspend fun getAssetFileHash(filePath: String): FileHash? =
        withContext(dispatchersProvider.io()) {
            database.bundledAssetEntityQueries.getByFilePath(filePath) { path, hash ->
                FileHash(path, hash.orEmpty())
            }.executeAsOneOrNull()
        }

    suspend fun saveAssetFileHash(fileHash: FileHash) = withContext(dispatchersProvider.io()) {
        database.bundledAssetEntityQueries.insert(fileHash.path, fileHash.sha256)
    }

}
