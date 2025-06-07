package com.techbeloved.hymnbook.shared.playlist

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class DeletePlaylistUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(playlistId: Long): Unit = withContext(dispatchersProvider.io()) {
        database.playlistEntityQueries.delete(playlistId).await()
    }
}
