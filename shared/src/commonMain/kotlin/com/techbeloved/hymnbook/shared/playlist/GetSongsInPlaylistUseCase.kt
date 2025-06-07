package com.techbeloved.hymnbook.shared.playlist

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.playlist.SongInPlaylist
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetSongsInPlaylistUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(playlistId: Long): List<SongInPlaylist> =
        withContext(dispatchersProvider.io()) {
            database.playlistSongsQueries.getSongsInPlaylist(playlistId) { id, title, alternateTitle ->
                SongInPlaylist(
                    id = id,
                    title = title,
                    alternateTitle = alternateTitle,
                    playlistId = playlistId,
                )
            }.executeAsList()
        }
}
