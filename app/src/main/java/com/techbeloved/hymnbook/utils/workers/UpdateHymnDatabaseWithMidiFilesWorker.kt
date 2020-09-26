package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import io.reactivex.Single
import timber.log.Timber
import java.io.File

class UpdateHymnDatabaseWithMidiFilesWorker @WorkerInject constructor(@Assisted context: Context, @Assisted params: WorkerParameters, private val hymnsRepository: HymnsRepository) : RxWorker(context, params) {
    override fun createWork(): Single<Result> {
        makeStatusNotification("Updating database", applicationContext)

        return Single.create { emitter ->
            val midiFilesDirectory = inputData.getString(KEY_UNZIP_FILES_DIRECTORY)
                    ?: File(applicationContext.getExternalFilesDir(null), applicationContext.getString(R.string.file_path_midi)).absolutePath
            val directory = File(midiFilesDirectory)
            var numberOfFiles: Int = 0
            for (fileName in (directory.list { dir, name ->
                name.contains("hymn")
                        && name.endsWith(".mid", true)
            } ?: emptyArray())) {
                // file name is of the form hymn_233.mid
                val hymnId = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf(".")).toIntOrNull()
                val fullMidiPath = midiFilesDirectory.plus("/").plus(fileName)
                hymnId?.let { id ->
                    hymnsRepository.updateHymnMidiPath(id, fullMidiPath)
                    numberOfFiles++
                }
                Timber.i("Updating midi for hymn: %s, %s", hymnId, fullMidiPath)
            }

            val outputData = workDataOf(KEY_NUMBER_OF_HYMN_MIDIS_UPDATED to numberOfFiles)
            emitter.onSuccess(Result.success(outputData))
        }

    }

}

const val KEY_NUMBER_OF_HYMN_MIDIS_UPDATED = "numberOfMidisUpdated"