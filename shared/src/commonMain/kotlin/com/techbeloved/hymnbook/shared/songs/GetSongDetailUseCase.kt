package com.techbeloved.hymnbook.shared.songs

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetSongDetailUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {

    suspend operator fun invoke(songId: Long) =
        withContext(dispatchersProvider.io()) {
            database.songEntityQueries.getSongById(songId).executeAsOne()
        }
}
