package com.techbeloved.hymnbook.usecases

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.techbeloved.hymnbook.utils.workers.ExtractBundledAssetsWorker
import com.techbeloved.hymnbook.utils.workers.UpdateHymnDatabaseWithMidiFilesWorker
import javax.inject.Inject

class ExtractBundledAssetsUseCase @Inject constructor(
    private val workManager: WorkManager,
) {
    operator fun invoke() {
        extractBundledArchive()
    }

    private fun extractBundledArchive() {
        val extractBundledAssetsWorker = OneTimeWorkRequestBuilder<ExtractBundledAssetsWorker>()
            .build()

        val updateHymnDatabaseMidiWork =
            OneTimeWorkRequestBuilder<UpdateHymnDatabaseWithMidiFilesWorker>()
                .build()

        workManager.beginUniqueWork(
            MIDI_ARCHIVE_DOWNLOAD_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            extractBundledAssetsWorker,
        ).then(updateHymnDatabaseMidiWork)
            .enqueue()
    }
}

const val MIDI_ARCHIVE_DOWNLOAD_WORK_NAME = "midiArchiveUnzip"
