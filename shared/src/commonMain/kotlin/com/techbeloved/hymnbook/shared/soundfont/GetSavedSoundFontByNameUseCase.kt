package com.techbeloved.hymnbook.shared.soundfont

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.file.FileHash
import com.techbeloved.hymnbook.shared.model.soundfont.SavedSoundFont
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject

internal class GetSavedSoundFontByNameUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {

    suspend operator fun invoke(fileName: String) =
        withContext(dispatchersProvider.io()) {
            database.soundFontEntityQueries.byName(fileName) { fileName, fileHash, displayName,
                                                               downloadedDate, fileSize ->
                SavedSoundFont(
                    displayName = displayName,
                    fileHash = FileHash(path = fileName, sha256 = fileHash),
                    downloadedDate = downloadedDate.toLocalDateTime(
                        TimeZone.currentSystemDefault(),
                    ),
                    fileSize = fileSize,
                )
            }.executeAsOneOrNull()
        }
}
