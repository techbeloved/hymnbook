package com.techbeloved.hymnbook.shared.playlist

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.time.InstantProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class UpdatePlaylistUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
    private val instantProvider: InstantProvider,
) {

    suspend operator fun invoke(
        playlistId: Long,
        name: String,
        description: String?,
        imageUrl: String?
    ): Long = withContext(dispatchersProvider.io()) {
        database.playlistEntityQueries.update(
            playlistId = playlistId,
            name = name,
            description = description,
            imageUrl = imageUrl,
            modified = instantProvider.get(),
        ).await()
        playlistId
    }

}
