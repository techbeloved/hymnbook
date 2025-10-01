package com.techbeloved.hymnbook.shared.songshare

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.AppHost
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.songbooks.GetSongbookEntriesForSongUseCase
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.parameters
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetSongShareDataUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
    private val getSongbookEntriesForSongUseCase: GetSongbookEntriesForSongUseCase,
) {
    suspend operator fun invoke(songId: Long): ShareAppData =
        withContext(dispatchersProvider.io()) {
            val song = database.songEntityQueries.getSongById(songId).executeAsOne()
            val songbookEntries = getSongbookEntriesForSongUseCase(songId).firstOrNull()
            ShareAppData(
                title = "Sharing song: ${song.title}",
                description = "${song.title} || ${songbookEntries?.songbook} #${songbookEntries?.entry}",
                url = URLBuilder(
                    protocol = URLProtocol.HTTPS,
                    host = AppHost,
                    parameters = parameters {
                        append("songbook", songbookEntries?.songbook ?: "")
                        append("entry", songbookEntries?.entry ?: "")
                    }).buildString(),
            )
        }
}
