package com.techbeloved.hymnbook.shared.sheetmusic

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.ext.sheetsDir
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.SheetMusic
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetAvailableSheetMusicForSongUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
    private val fileSystemProvider: OkioFileSystemProvider,
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
