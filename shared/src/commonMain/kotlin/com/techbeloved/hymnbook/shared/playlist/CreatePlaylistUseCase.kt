package com.techbeloved.hymnbook.shared.playlist

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.time.InstantProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class CreatePlaylistUseCase @Inject constructor(
    private val database: Database,
    private val instantProvider: InstantProvider,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(name: String, description: String?, imageUrl: String?): Long =
        withContext(dispatchersProvider.io()) {
            val time = instantProvider.get()
            database.playlistEntityQueries.transactionWithResult(noEnclosing = true) {
                database.playlistEntityQueries.insert(
                    id = null,
                    name = name,
                    description = description,
                    image_url = imageUrl,
                    created = time,
                    modified = time,
                )
                database.playlistEntityQueries.lastInsertRowId().executeAsOne()
            }
        }
}
