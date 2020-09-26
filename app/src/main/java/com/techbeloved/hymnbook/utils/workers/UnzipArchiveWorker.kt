package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.FileManager
import io.reactivex.Single
import timber.log.Timber
import java.io.File

/**
 * Takes care of unzipping a single archive. Input data should be supplied with the key [KEY_DOWNLOADED_ARCHIVE]
 *  for the archive path and [KEY_UNZIP_FILES_DIRECTORY] for the location where the files will be unzipped
 */
class UnzipArchiveWorker @WorkerInject constructor(@Assisted context: Context, @Assisted params: WorkerParameters, private val fileManager: FileManager) : RxWorker(context, params) {
    override fun createWork(): Single<Result> {
        Timber.i("Unzip work: onGoing")
        makeStatusNotification("Unzipping midi archive", applicationContext)

        val archivePath = inputData.getString(KEY_DOWNLOADED_ARCHIVE)
        val unzipDirPath = inputData.getString(KEY_UNZIP_FILES_DIRECTORY)
                ?: File(applicationContext.getExternalFilesDir(null), applicationContext.getString(R.string.file_path_artifacts)).absolutePath
        if (archivePath == null) return Single.just(Result.failure())
        return fileManager.unzipFile(archivePath, unzipDirPath)
                .map { unzippedFilesLocation ->
                    val outputData = Data.Builder()
                            .putString(KEY_UNZIP_FILES_DIRECTORY, unzippedFilesLocation)
                    Result.success(outputData.build())
                }
    }

}

const val KEY_UNZIP_FILES_DIRECTORY = "unzipFilesDirectory"
