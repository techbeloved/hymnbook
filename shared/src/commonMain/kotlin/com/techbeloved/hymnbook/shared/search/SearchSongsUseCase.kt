package com.techbeloved.hymnbook.shared.search

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.coroutines.withContext

internal class SearchSongsUseCase(
    private val database: Database = Injector.database,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
) {
    suspend operator fun invoke(searchQuery: String): List<SongTitle> =
        withContext(dispatchersProvider.io()) {
            database.songEntityQueries.searchSongs(searchQuery, ::SongTitle)
                .executeAsList()
        }
}
