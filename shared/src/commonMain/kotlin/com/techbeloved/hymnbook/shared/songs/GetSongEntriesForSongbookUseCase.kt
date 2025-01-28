package com.techbeloved.hymnbook.shared.songs

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import com.techbeloved.hymnbook.shared.model.SongPageEntry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetSongEntriesForSongbookUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {

    suspend operator fun invoke(songbookEntry: SongBookEntry): ImmutableList<SongPageEntry> =
        withContext(dispatchersProvider.io()) {
            database.songbookSongsQueries
                .getSongbookEntries(songbookEntry.songbook) { songbook, song_id, entry ->
                    SongPageEntry(
                        checkNotNull(song_id),
                        SongBookEntry(checkNotNull(songbook), checkNotNull(entry)),
                    )
                }.executeAsList().toImmutableList()
        }
}
