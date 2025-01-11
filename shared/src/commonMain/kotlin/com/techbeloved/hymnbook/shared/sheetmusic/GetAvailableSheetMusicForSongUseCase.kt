package com.techbeloved.hymnbook.shared.sheetmusic

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.ext.sheetsDir
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.defaultOkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.SheetMusic
import kotlinx.coroutines.withContext

internal class GetAvailableSheetMusicForSongUseCase(
    private val database: Database = Injector.database,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
    private val fileSystemProvider: OkioFileSystemProvider = defaultOkioFileSystemProvider,
) {

    suspend operator fun invoke(songId: Long): SheetMusic? = withContext(dispatchersProvider.io()) {
        val fileSystem = fileSystemProvider.get()
        val availableSheetMusic =
            database.sheetMusicEntityQueries.sheetMusicForSong(songId).executeAsOneOrNull()
        if (availableSheetMusic?.file_path != null && availableSheetMusic.type != null) {
            val absolutePath = fileSystem.sheetsDir() / availableSheetMusic.file_path
            SheetMusic(
                songId = songId,
                relativePath = absolutePath.relativeTo(fileSystem.userData),
                absolutePath = absolutePath,
                type = availableSheetMusic.type,
            )
        } else {
            null
        }
    }
}
