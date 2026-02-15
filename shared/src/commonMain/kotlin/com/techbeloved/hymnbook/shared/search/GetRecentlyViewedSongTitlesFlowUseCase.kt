package com.techbeloved.hymnbook.shared.search

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

internal class GetRecentlyViewedSongTitlesFlowUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke() =
        database.recentlyViewedSongsEntityQueries.getRecentlyViewedSongs(lmt = 10).asFlow()
            .mapToList(dispatchersProvider.io())
            .map { query ->
                query.map {
                    SongTitle(
                        id = it.id,
                        title = it.title,
                        alternateTitle = it.alternate_title,
                        songbook = it.songbook,
                        songbookEntry = it.songbook_entry,
                    )
                }
            }
}
