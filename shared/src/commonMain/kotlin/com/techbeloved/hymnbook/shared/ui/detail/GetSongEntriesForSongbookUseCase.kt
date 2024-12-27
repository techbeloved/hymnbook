package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.model.SongBookEntry
import com.techbeloved.hymnbook.shared.model.SongPageEntry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.withContext

internal class GetSongEntriesForSongbookUseCase(
    private val database: Database = Injector.database,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
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
