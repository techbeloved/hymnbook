package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.techbeloved.hymnbook.di.Injection

class UpdateMidiVersionPrefWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val archiveVersion = inputData.getInt(KEY_FIREBASE_ARCHIVE_VERSION, 0)
        Injection.provideSharePrefsRepo.midiArchiveVersion(archiveVersion)
        return Result.success()
    }

}