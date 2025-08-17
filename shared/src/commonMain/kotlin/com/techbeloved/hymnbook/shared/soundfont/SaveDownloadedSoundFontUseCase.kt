package com.techbeloved.hymnbook.shared.soundfont

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.soundfont.SavedSoundFont
import com.techbeloved.hymnbook.shared.time.InstantProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class SaveDownloadedSoundFontUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
    private val instantProvider: InstantProvider,
) {
    suspend operator fun invoke(soundFont: SavedSoundFont) =
        withContext(dispatchersProvider.io()) {
            database.soundFontEntityQueries.insert(
                fileName = soundFont.fileHash.path,
                fileHash = soundFont.fileHash.sha256,
                displayName = soundFont.displayName,
                downloadedDate = instantProvider.get(),
                fileSize = soundFont.fileSize,
            ).await()
        }
}
