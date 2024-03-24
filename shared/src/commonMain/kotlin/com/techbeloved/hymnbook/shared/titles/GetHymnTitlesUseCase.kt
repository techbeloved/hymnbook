package com.techbeloved.hymnbook.shared.titles

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.coroutines.withContext

internal class GetHymnTitlesUseCase(
    private val database: Database = Injector.database,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
) {
    suspend operator fun invoke() = withContext(dispatchersProvider.io()) {
        database.songEntityQueries.getAllTitles(::SongTitle)
            .executeAsList()
    }
}
