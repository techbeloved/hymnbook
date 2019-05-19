package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.techbeloved.hymnbook.di.Injection
import io.reactivex.Single
import timber.log.Timber

/**
 * Should use this to schedule hymns midi download and database sync. Periodically,
 * like once a week checks for new update. If there is any update, downloads, it, extracts it and populate the database
 */
class MidiSyncWorker(context: Context, params: WorkerParameters) : RxWorker(context, params) {

    override fun createWork(): Single<Result> {
        Timber.i("MidiSyncWork: onGoing work")
        makeStatusNotification("Checking midi archive version", applicationContext)
        return Injection.provideSharePrefsRepo.midiArchiveVersion()
                .flatMapObservable { currentVersion ->
                    Injection.provideOnlineRepo.latestMidiArchive()
                            .filter { onlineMidi -> onlineMidi.version > currentVersion }
                }.firstOrError()
                .doOnSuccess { Timber.i("New version of archive available! Proceeding with download") }
                .doOnError { Timber.i(it, "No new version of archive available") }
                .map { onlineMidi ->
                    val archivePath = onlineMidi.url.substring(onlineMidi.url.lastIndexOf("tunes"))
                    val outputData = workDataOf(
                            KEY_FIREBASE_ARCHIVE_PATH to archivePath,
                            KEY_FIREBASE_ARCHIVE_VERSION to onlineMidi.version)
                    Result.success(outputData)
                }
    }

}

/*
1. Gets the latest version from firebase and compare with one in shared preferences
2. If not equal, start a download
3. On download complete, unzip the files
4. On unzip complete, loop through the files and update the relevant columns in database
 */

/**
 * Worker [Data] key for supplying the default preferences name to use. We make use of this so that
 * tests can have their own preferences file without tempering with the main app preferences
 */
const val KEY_DEFAULT_PREFERENCE_NAME = "defaultPreferenceName"
/**
 * Worker [Data] key for supplying the midiVersionPreference key
 */
const val KEY_PREF_MIDI_VERSION = "keyMidiVersion"

const val KEY_FIREBASE_ARCHIVE_COLLECTION = "archiveCollection"

const val KEY_FIREBASE_ARCHIVE_VERSION = "midiFirebaseArchiveVersion"

