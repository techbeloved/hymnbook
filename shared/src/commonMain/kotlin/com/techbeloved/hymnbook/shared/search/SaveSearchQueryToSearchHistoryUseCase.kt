package com.techbeloved.hymnbook.shared.search

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class SaveSearchQueryToSearchHistoryUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(query: String) = withContext(dispatchersProvider.io()){
        database.searchHistoryEntityQueries.upsert(query)
            .await()
    }
}
