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
    suspend operator fun invoke(searchQuery: String): List<SongTitle> =
        withContext(dispatchersProvider.io()) {
            // check if search query contains only digits, then use the searchSongbookEntry
            if (searchQuery.matches(regex = "\\d+".toRegex())) {
                database.songEntityQueries.searchSongbookEntry(searchQuery, ::SongTitle)
                    .executeAsList()
            } else {
                database.songEntityQueries.searchSongs(searchQuery, ::SongTitle)
                    .executeAsList()
            }
        }
}
