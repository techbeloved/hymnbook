package com.techbeloved.hymnbook.shared.search

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

internal class FilterSearchHistoryFlowUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke(query: String) = if (query.isBlank()) flowOf(emptyList())
    else database.searchHistoryEntityQueries.filter(query, lmt = 10) { query, _ -> query }
        .asFlow()
        .mapToList(dispatchersProvider.io())
}
