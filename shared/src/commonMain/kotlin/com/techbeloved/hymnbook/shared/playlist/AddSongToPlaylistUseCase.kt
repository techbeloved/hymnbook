package com.techbeloved.hymnbook.shared.playlist

import com.techbeloved.hymnbook.Database
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class AddSongToPlaylistUseCase @Inject constructor(
    private val database: Database,
) {

    suspend operator fun invoke(songId: Long, playlistId: Long) = withContext(NonCancellable) {
        database.playlistSongsQueries.insert(playlist_id = playlistId, song_id = songId).await()
    }

}
