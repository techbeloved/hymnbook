package com.techbeloved.hymnbook.shared.songbooks

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.ext.formattedSongbookEntries
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetSongbookEntriesForSongUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
){
    suspend operator fun invoke(songId: Long) = withContext(dispatchersProvider.io()) {
        database.songEntityQueries.getSongbookEntries(id = songId) { it?.formattedSongbookEntries() ?: emptySet() }
            .executeAsOne()
    }
}
