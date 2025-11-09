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
    suspend operator fun invoke(searchQuery: String, songbook: String?): List<SongTitle> {
        val sanitizedQuery = searchQuery.trim().replace("\"", "\"\"")
        val ftsQuery = if (sanitizedQuery.isNotBlank()) "\"$sanitizedQuery\"" else ""

        return withContext(dispatchersProvider.io()) {
            // check if search query contains only digits, then use the searchSongbookEntry
            if (sanitizedQuery.matches(regex = "\\d+".toRegex())) {
                database.songEntityQueries.searchSongbookEntry(
                    search = sanitizedQuery,
                    songbook = songbook,
                    mapper = ::SongTitle,
                ).executeAsList()
            } else if (ftsQuery.isNotEmpty()) { // Ensure we don't run an empty FTS query
                database.songEntityQueries.searchSongs(
                    search = ftsQuery,
                    songbook = songbook,
                    mapper = ::SongTitle,
                ).executeAsList()
            } else {
                // If the query is blank after trimming, return an empty list
                emptyList()
            }
        }
    }
}

