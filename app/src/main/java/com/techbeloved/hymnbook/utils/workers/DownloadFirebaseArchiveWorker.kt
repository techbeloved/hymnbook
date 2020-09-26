package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.techbeloved.hymnbook.data.download.Downloader
import io.reactivex.Single
import timber.log.Timber
import java.io.File

/**
 * A worker that downloads archive
 */
class DownloadFirebaseArchiveWorker @WorkerInject constructor(@Assisted context: Context, @Assisted params: WorkerParameters, private val downloader: Downloader) : RxWorker(context, params) {

    override fun createWork(): Single<Result> {
        Timber.i("Downloading archive: onGoing")
        makeStatusNotification("Downloading midi archive", applicationContext)

        val archivePath = inputData.getString(KEY_FIREBASE_ARCHIVE_PATH)
        val destination = inputData.getString(KEY_ARCHIVE_DESTINATION)
        if (archivePath == null || destination == null) return Single.just(Result.failure())
        val outFile = File(destination)
        return downloader.downloadFirebaseArchive(archivePath, outFile)
                .map { downloaded ->
                    val outPutData = Data.Builder().putString(KEY_DOWNLOADED_ARCHIVE, downloaded)
                    Result.success(outPutData.build())
                }
                .onErrorReturn {
                    Timber.w(it)
                    Result.retry()
                }
    }


}

const val KEY_FIREBASE_ARCHIVE_PATH = "firebaseArchivePath"
const val KEY_ARCHIVE_DESTINATION = "archiveDestination"
const val KEY_DOWNLOADED_ARCHIVE = "downloadedArchive"