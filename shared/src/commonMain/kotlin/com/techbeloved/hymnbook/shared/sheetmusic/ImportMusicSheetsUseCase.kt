package com.techbeloved.hymnbook.shared.sheetmusic

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.ext.extension
import com.techbeloved.hymnbook.shared.ext.sheetsDir
import com.techbeloved.hymnbook.shared.files.HashFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.SheetMusic
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import okio.Path

internal class ImportMusicSheetsUseCase @Inject constructor(
    private val database: Database,
    private val fileSystemProvider: OkioFileSystemProvider,
    private val dispatchersProvider: DispatchersProvider,
    private val hashFileUseCase: HashFileUseCase,
) {
    suspend operator fun invoke(directory: Path, prefix: String, songbook: String) =
        withContext(dispatchersProvider.io()) {
            val sharedFileSystem = fileSystemProvider.get()
            val fileSystem = sharedFileSystem.fileSystem
            fileSystem.createDirectory(sharedFileSystem.sheetsDir())
            runCatching {
                fileSystem.listRecursively(directory)
                    .filter { supportedSheetMusicFormats.contains(it.extension().lowercase()) }
                    .forEach { sheetPath ->
                        val entry = sheetPath.name.removePrefix(prefix).substringBeforeLast(".")

                        val songEntry = database.songbookSongsQueries.getSongbookEntry(
                            songbook = songbook,
                            entry = entry
                        ).executeAsOneOrNull()
                        val fileExtension = sheetPath.extension().lowercase()
                        if (songEntry != null) {
                            database.sheetMusicEntityQueries.insert(
                                song_id = songEntry.song_id,
                                file_path = sheetPath.name,
                                file_hash = hashFileUseCase(sheetPath).sha256,
                                type = if (fileExtension in pdfSheetMusicFormats) {
                                    SheetMusic.Type.Pdf
                                } else {
                                    SheetMusic.Type.Image
                                },
                            )
                        }
                    }
            }
        }
}
