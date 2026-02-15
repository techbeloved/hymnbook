package com.techbeloved.hymnbook.shared.search

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import me.tatarka.inject.annotations.Inject

internal class GetLatestSearchHistoryFlowUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke() =
        database.searchHistoryEntityQueries.getLatest(lmt = 5) { searchQuery, _ -> searchQuery }
            .asFlow()
            .mapToList(dispatchersProvider.io())
}
