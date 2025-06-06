package com.techbeloved.hymnbook.shared.titles

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetFilteredSongTitlesUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(songFilter: SongFilter): List<SongTitle> = withContext(dispatchersProvider.io()) {
        val query = when {
            songFilter.byTopicsAndSongbooks -> {
                database.songEntityQueries.filterSongsByTopicsAndSongbooks(
                    topicNames = songFilter.topics,
                    songbookNames = songFilter.songbooks,
                    orderByTitle = songFilter.orderByTitle,
                    mapper = ::SongTitle,
                )
            }

            songFilter.byTopicsOnly -> {
                database.songEntityQueries.filterSongsByTopics(
                    topicNames = songFilter.topics,
                    orderByTitle = songFilter.orderByTitle,
                    mapper = ::SongTitle,
                )
            }

            songFilter.bySongbooks -> {
                database.songEntityQueries.filterSongsBySongbooks(
                    songbookNames = songFilter.songbooks,
                    orderByTitle = songFilter.orderByTitle,
                    mapper = ::SongTitle,
                )
            }

            else -> {
                database.songEntityQueries.filterSongs(
                    orderByTitle = songFilter.orderByTitle,
                    mapper = ::SongTitle,
                )
            }
        }
        query.executeAsList()
    }
}
