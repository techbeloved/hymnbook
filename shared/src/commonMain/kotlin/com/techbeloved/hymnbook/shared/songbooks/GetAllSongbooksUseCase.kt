package com.techbeloved.hymnbook.shared.songbooks

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.SongbookEntity
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetAllSongbooksUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(): List<SongbookEntity> = withContext(dispatchersProvider.io()) {
        database.songbookEntityQueries.getAll().executeAsList()
    }
}
