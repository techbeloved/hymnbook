package com.techbeloved.hymnbook.shared.search

import app.cash.sqldelight.coroutines.asFlow
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

internal class GetLatestSearchHistoryFlowUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke() =
        database.searchHistoryEntityQueries.getLatest(lmt = 5) { searchQuery, _ -> searchQuery }
            .asFlow()
            .map { it.executeAsList() }
            .flowOn(dispatchersProvider.io())
}
