package com.techbeloved.hymnbook.shared.media

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.ext.tunesDir
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.ext.authors
import com.techbeloved.hymnbook.shared.model.ext.songbookEntries
import com.techbeloved.media.AudioItem
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetAvailableMediaForSongUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
    private val fileSystemProvider: OkioFileSystemProvider,
) {
    suspend operator fun invoke(songId: Long): List<AudioItem> {
        return withContext(dispatchersProvider.io()) {
            val fileSystem = fileSystemProvider.get()
            val song = database.songEntityQueries.getSongById(songId).executeAsOne()
            val availableMedia = database.mediaFileQueries.mediaForSong(songId).executeAsList()
            availableMedia.mapNotNull { item ->
                if (item.file_path != null) {
                    val songBookEntry = song.songbookEntries().firstOrNull()
                    val absolutePath = fileSystem.tunesDir() / item.file_path
                    AudioItem(
                        absolutePath = absolutePath.toString(),
                        relativePath = absolutePath.relativeTo(fileSystem.userData).toString(),
                        title = song.title,
                        artist = song.authors().firstOrNull()?.name.orEmpty(),
                        album = songBookEntry?.songbook.orEmpty(),
                        mediaId = songBookEntry?.let { "${it.songbook}||${it.entry}" }.orEmpty()
                    )
                } else {
                    null
                }
            }
        }
    }
}
