package com.techbeloved.hymnbook.shared.media

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.ext.extension
import com.techbeloved.hymnbook.shared.files.HashFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.defaultOkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.MediaType
import kotlinx.coroutines.withContext
import okio.Path

internal class ImportMediaFilesUseCase(
    private val database: Database = Injector.database,
    private val fileSystemProvider: OkioFileSystemProvider = defaultOkioFileSystemProvider,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
    private val hashFileUseCase: HashFileUseCase = HashFileUseCase(),
) {
    suspend operator fun invoke(directory: Path) = withContext(dispatchersProvider.io()) {
        val fileSystem = fileSystemProvider.get().fileSystem
        runCatching {
            fileSystem.listRecursively(directory)
                .filter { supportedAudioFormats.contains(it.extension()) }
                .forEach { tunesPath ->
                    // expected format of media files
                    // songbookname_entry.extension
                    // E.g. Watchman (Hymn)_123.mp3
                    val (songbook, entry) = tunesPath.name.substringBeforeLast(".")
                        .split("_")
                    val songEntry = database.songbookSongsQueries.getSongbookEntry(
                        songbook = songbook,
                        entry = entry
                    ).executeAsOne()
                    database.mediaFileQueries.insert(
                        song_id = songEntry.song_id,
                        file_path = tunesPath.toString(),
                        file_hash = hashFileUseCase(tunesPath).sha256,
                        type = if (tunesPath.name.endsWith(".mid")) {
                            MediaType.Midi
                        } else {
                            MediaType.Audio
                        }
                    )
                }
        }
    }
}
