package com.techbeloved.hymnbook.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.utils.Decompress
import timber.log.Timber
import java.io.File

// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_UNZIP_CATALOG = "com.techbeloved.hymnbook.services.action.UNZIP_CATALOG"

private const val EXTRA_DESTINATION = "com.techbeloved.hymnbook.services.extra.DESTINATION"
private const val EXTRA_ZIP_FILE_NAME = "com.techbeloved.hymnbook.services.extra.ZIP_FILE"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * helper methods.
 */
class FileManagerService : IntentService("FileManagerService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_UNZIP_CATALOG -> {
                val destination = intent.getStringExtra(EXTRA_DESTINATION)
                val sourceZipFile = intent.getStringExtra(EXTRA_ZIP_FILE_NAME)
                handleActionUnzipFile(destination, sourceZipFile)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionUnzipFile(destination: String, sourceZipFile: String) {
        Timber.i("About decompressing file: %s", sourceZipFile)
        val d = Decompress(sourceZipFile, destination)
        d.unzip()
        File(sourceZipFile).delete()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)
        if (destination.contains("catalog", true)) {
            val hymnCatalogReadyPref = rxPreferences.getBoolean(getString(R.string.pref_key_hymn_catalog_ready), false)
            hymnCatalogReadyPref.set(true)
        } else if (destination.contains("midi")) {
            val midiReadyPref = rxPreferences.getBoolean(getString(R.string.pref_key_hymn_midi_files_ready), false)
            midiReadyPref.set(true)
        }
        Timber.i("Done decompressing files")
    }


    companion object {
        /**
         * Starts this service to perform action Unzip File with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionUnzipFile(context: Context, destination: String, zipFile: String) {
            val intent = Intent(context, FileManagerService::class.java).apply {
                action = ACTION_UNZIP_CATALOG
                putExtra(EXTRA_DESTINATION, destination)
                putExtra(EXTRA_ZIP_FILE_NAME, zipFile)
            }
            context.startService(intent)
        }
    }
}
