package com.techbeloved.hymnbook.shared.media

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.ext.extension
import com.techbeloved.hymnbook.shared.files.HashFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.MediaType
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import okio.Path

internal class ImportMediaFilesUseCase @Inject constructor(
    private val database: Database,
    private val fileSystemProvider: OkioFileSystemProvider,
    private val dispatchersProvider: DispatchersProvider,
    private val hashFileUseCase: HashFileUseCase,
) {
    suspend operator fun invoke(directory: Path) = withContext(dispatchersProvider.io()) {
        val fileSystem = fileSystemProvider.get().fileSystem
        runCatching {
            fileSystem.listRecursively(directory)
                .filter { supportedAudioFormats.contains(it.extension().lowercase()) }
                .forEach { tunesPath ->
                    // expected format of media files
                    // songbookname_entry.extension
                    // E.g. Watchman (Hymn)_123.mp3
                    val (songbook, entry) = tunesPath.name.substringBeforeLast(".")
                        .split("_")
                    val songEntry = database.songbookSongsQueries.getSongbookEntry(
                        songbook = songbook,
                        entry = entry
                    ).executeAsOneOrNull()
                    if (songEntry != null) {
                        database.mediaFileQueries.insert(
                            song_id = songEntry.song_id,
                            file_path = tunesPath.name, // only save the relative path.
                            // We will resolve this at runtime.
                            // It's not a good idea to save the absolute path, especially on iOS
                            file_hash = hashFileUseCase(tunesPath).sha256,
                            type = if (tunesPath.name.endsWith(".mid", ignoreCase = true)) {
                                MediaType.Midi
                            } else {
                                MediaType.Audio
                            }
                        )
                    }
                }
        }
    }
}
