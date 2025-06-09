package com.techbeloved.hymnbook.shared.songbooks

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import me.tatarka.inject.annotations.Inject

internal class GetAllSongbooksUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke() =
        database.songbookEntityQueries.getAll().asFlow().mapToList(dispatchersProvider.io())
}
