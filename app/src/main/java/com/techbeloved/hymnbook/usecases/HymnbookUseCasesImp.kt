package com.techbeloved.hymnbook.usecases

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.utils.workers.DownloadFirebaseArchiveWorker
import com.techbeloved.hymnbook.utils.workers.HymnSyncWorker
import com.techbeloved.hymnbook.utils.workers.KEY_ARCHIVE_DESTINATION
import com.techbeloved.hymnbook.utils.workers.KEY_UNZIP_FILES_DIRECTORY
import com.techbeloved.hymnbook.utils.workers.MidiSyncWorker
import com.techbeloved.hymnbook.utils.workers.UnzipArchiveWorker
import com.techbeloved.hymnbook.utils.workers.UpdateHymnDatabaseWithMidiFilesWorker
import com.techbeloved.hymnbook.utils.workers.UpdateMidiVersionPrefWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Observable
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HymnbookUseCasesImp @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepo: SharedPreferencesRepo,
    private val workManager: WorkManager
) : HymnbookUseCases {
    override fun downloadLatestHymnMidiArchive() {
        /*
        1. Schedule a work request
        2. Chain work requests that will download, unzip and update database
         */
        scheduleDownloadMidiArchiveWork()
    }

    override fun appFirstStart(): Observable<Boolean> {
        return preferencesRepo.isFirstStart()
    }

    override fun updateAppFirstStart(firstStart: Boolean) {
        preferencesRepo.setFirstStart(false)
    }

    private fun scheduleDownloadMidiArchiveWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val midiSyncWork = OneTimeWorkRequestBuilder<MidiSyncWorker>()
            .setConstraints(constraints)
            .build()

        // Configure archive downloader
        val cacheDir = context.externalCacheDir
        val archiveDestinationDir = File(cacheDir, context.getString(R.string.file_path_downloads))
        val createDownloadDir = archiveDestinationDir.mkdir()
        Timber.i(
            "Created download dir: %s, %s",
            createDownloadDir,
            archiveDestinationDir.absolutePath
        )
        val archiveDestination =
            File(archiveDestinationDir, context.getString(R.string.file_path_midi_archive))
        val archiveWorkInput = Data.Builder()
            .putString(KEY_ARCHIVE_DESTINATION, archiveDestination.absolutePath)
            .build()
        val archiveDownloadWork =
            OneTimeWorkRequestBuilder<DownloadFirebaseArchiveWorker>()
                .setConstraints(constraints)
                .setInputData(archiveWorkInput)
                .build()

        // Configure Unzip worker
        val unzipFilesDir =
            File(context.getExternalFilesDir(null), context.getString(R.string.file_path_midi))
        unzipFilesDir.mkdir()
        val unzipWorkInput = Data.Builder()
            .putString(KEY_UNZIP_FILES_DIRECTORY, unzipFilesDir.absolutePath)
            .build()
        val unzipArchiveWork =
            OneTimeWorkRequestBuilder<UnzipArchiveWorker>()
                .setInputData(unzipWorkInput)
                .build()

        val updateHymnDatabaseMidiWork =
            OneTimeWorkRequestBuilder<UpdateHymnDatabaseWithMidiFilesWorker>()
                .build()

        val updateMidiVersionPrefWork =
            OneTimeWorkRequestBuilder<UpdateMidiVersionPrefWorker>()
                .build()

        val syncOnlineHymns = OneTimeWorkRequestBuilder<HymnSyncWorker>()
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.DAYS)
            .build()

        workManager.beginUniqueWork(
            MIDI_ARCHIVE_DOWNLOAD_WORK_NAME,
            ExistingWorkPolicy.KEEP, midiSyncWork
        )
            .then(archiveDownloadWork)
            .then(unzipArchiveWork)
            .then(updateHymnDatabaseMidiWork)
            .then(updateMidiVersionPrefWork)
            .then(syncOnlineHymns)
            .enqueue()
    }
}

const val MIDI_ARCHIVE_DOWNLOAD_WORK_NAME = "midiArchiveDownloadAndUnzip"