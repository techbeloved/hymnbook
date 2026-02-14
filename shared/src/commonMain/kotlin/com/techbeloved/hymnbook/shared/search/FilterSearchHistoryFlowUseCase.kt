package com.techbeloved.hymnbook.shared.search

import app.cash.sqldelight.coroutines.asFlow
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

internal class FilterSearchHistoryFlowUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke(query: String) =
        database.searchHistoryEntityQueries.filter(query, lmt = 10) { query, _ -> query }
            .asFlow()
            .map { it.executeAsList() }
            .flowOn(dispatchersProvider.io())
}
