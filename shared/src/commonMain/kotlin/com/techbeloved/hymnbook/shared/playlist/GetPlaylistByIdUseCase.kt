package com.techbeloved.hymnbook.shared.playlist

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetPlaylistByIdUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(playlistId: Long): PlaylistItem =
        withContext(dispatchersProvider.io()) {
            database.playlistEntityQueries.getById(
                playlistId = playlistId,
            ) { id, name, description, imageUrl, created, modified, songCount ->
                PlaylistItem(
                    id = id,
                    name = name,
                    description = description,
                    imageUrl = imageUrl,
                    created = created,
                    updated = modified,
                    songCount = songCount,
                )

            }.executeAsOne()
        }
}
