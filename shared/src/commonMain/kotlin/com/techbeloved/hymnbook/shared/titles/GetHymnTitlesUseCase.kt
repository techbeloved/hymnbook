package com.techbeloved.hymnbook.shared.titles

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetHymnTitlesUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke() = withContext(dispatchersProvider.io()) {
        database.songEntityQueries.getAllTitles(::SongTitle)
            .executeAsList()
    }
}
