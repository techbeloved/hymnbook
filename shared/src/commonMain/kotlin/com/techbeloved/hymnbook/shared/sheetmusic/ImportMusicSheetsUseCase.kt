package com.techbeloved.hymnbook.shared.sheetmusic

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.ext.extension
import com.techbeloved.hymnbook.shared.ext.sheetsDir
import com.techbeloved.hymnbook.shared.files.HashFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.defaultOkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.SheetMusic
import kotlinx.coroutines.withContext
import okio.Path

internal class ImportMusicSheetsUseCase(
    private val database: Database = Injector.database,
    private val fileSystemProvider: OkioFileSystemProvider = defaultOkioFileSystemProvider,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
    private val hashFileUseCase: HashFileUseCase = HashFileUseCase(),
) {
    suspend operator fun invoke(directory: Path) = withContext(dispatchersProvider.io()) {
        val sharedFileSystem = fileSystemProvider.get()
        val fileSystem = sharedFileSystem.fileSystem
        fileSystem.createDirectory(sharedFileSystem.sheetsDir())
        runCatching {
            fileSystem.listRecursively(directory)
                .filter { supportedSheetMusicFormats.contains(it.extension().lowercase()) }
                .forEach { sheetPath ->
                    val (songbook, entry) = sheetPath.name.substringBeforeLast(".")
                        .split("_")

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
