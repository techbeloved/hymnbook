package com.techbeloved.hymnbook.shared.search

import app.cash.sqldelight.coroutines.asFlow
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

internal class GetRecentlyViewedSongTitlesFlowUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke() =
        database.recentlyViewedSongsEntityQueries.getRecentlyViewedSongs(lmt = 10).asFlow()
            .map { query ->
                query.executeAsList().map {
                    SongTitle(
                        id = it.id,
                        title = it.title,
                        alternateTitle = it.alternate_title,
                        songbook = it.songbook,
                        songbookEntry = it.songbook_entry,
                    )
                }
            }.flowOn(dispatchersProvider.io())
}
