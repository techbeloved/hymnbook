package com.techbeloved.hymnbook.shared.search

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class SearchSongsUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(searchQuery: String, songbook: String?): List<SongTitle> =
        withContext(dispatchersProvider.io()) {
            // check if search query contains only digits, then use the searchSongbookEntry
            if (searchQuery.matches(regex = "\\d+".toRegex())) {
                database.songEntityQueries.searchSongbookEntry(
                    search = searchQuery,
                    songbook = songbook,
                    mapper = ::SongTitle,
                )
            } else {
                database.songEntityQueries.searchSongs(
                    search = searchQuery,
                    songbook = songbook,
                    mapper = ::SongTitle,
                )
            }.executeAsList()
        }
}
