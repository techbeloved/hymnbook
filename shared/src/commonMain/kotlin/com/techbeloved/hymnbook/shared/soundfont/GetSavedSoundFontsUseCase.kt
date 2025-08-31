package com.techbeloved.hymnbook.shared.soundfont

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.file.FileHash
import com.techbeloved.hymnbook.shared.model.soundfont.SavedSoundFont
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject

internal class GetSavedSoundFontsUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke() =
        database.soundFontEntityQueries.selectAll { fileName, fileHash,
                                                    displayName, downloadedDate, fileSize ->
            SavedSoundFont(
                displayName = displayName,
                fileHash = FileHash(path = fileName, sha256 = fileHash),
                downloadedDate = downloadedDate.toLocalDateTime(TimeZone.currentSystemDefault()),
                fileSize = fileSize,
            )
        }.asFlow().mapToList(dispatchersProvider.io())
}
