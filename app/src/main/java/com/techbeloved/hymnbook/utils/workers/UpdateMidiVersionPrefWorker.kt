package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.techbeloved.hymnbook.data.SharedPreferencesRepo

class UpdateMidiVersionPrefWorker @WorkerInject constructor(@Assisted context: Context, @Assisted params: WorkerParameters, private val sharedPreferencesRepo: SharedPreferencesRepo) : Worker(context, params) {
    override fun doWork(): Result {
        val archiveVersion = inputData.getInt(KEY_FIREBASE_ARCHIVE_VERSION, 0)
        sharedPreferencesRepo.midiArchiveVersion(archiveVersion)
        return Result.success()
    }

}