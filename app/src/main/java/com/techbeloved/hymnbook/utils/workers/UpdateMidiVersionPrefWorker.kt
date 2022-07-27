package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateMidiVersionPrefWorker @AssistedInject constructor(@Assisted context: Context, @Assisted params: WorkerParameters, private val sharedPreferencesRepo: SharedPreferencesRepo) : Worker(context, params) {
    override fun doWork(): Result {
        val archiveVersion = inputData.getInt(KEY_FIREBASE_ARCHIVE_VERSION, 0)
        sharedPreferencesRepo.midiArchiveVersion(archiveVersion)
        return Result.success()
    }

}