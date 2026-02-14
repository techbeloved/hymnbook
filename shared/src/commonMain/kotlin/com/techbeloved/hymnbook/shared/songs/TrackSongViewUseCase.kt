package com.techbeloved.hymnbook.shared.songs

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class TrackSongViewUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(songId: Long, songbookEntry: SongBookEntry?) =
        withContext(dispatchersProvider.io()) {
            database.recentlyViewedSongsEntityQueries.upsert(
                song_id = songId,
                songbook = songbookEntry?.songbook,
                songbook_entry = songbookEntry?.entry,
            )
        }
}
