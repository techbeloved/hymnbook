package com.techbeloved.hymnbook.shared.playlist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

internal class GetPlaylistsUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke(): Flow<List<PlaylistItem>> =
        database.playlistEntityQueries.getAll { id, name, description, imageUrl, created, updated ->
            PlaylistItem(
                id = id,
                name = name,
                description = description,
                imageUrl = imageUrl,
                created = created,
                updated = updated,
            )
        }.asFlow().mapToList(context = dispatchersProvider.io())
}
