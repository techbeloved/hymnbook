package com.techbeloved.hymnbook.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.os.ResultReceiver
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.services.FileManagerService
import io.reactivex.Observable
import java.io.File

class FileManagerImp(val context: Context, val sharedPreferences: SharedPreferencesRepo) : FileManager {
    override fun unzipFile(source: String, destinationDir: String): Observable<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllFilesInDir(dir: String): Observable<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun copyFile(source: String, destination: String, newFileName: String): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun processDownloadZipFiles(vararg zipFiles: String): Observable<ProcessZipStatus> {

        for (zipFile in zipFiles) {
            val file = File(zipFile)
            if (file.isFile && file.extension == "zip") {
                val destination = when {
                    file.name.contains("catalog") -> File(context.getExternalFilesDir(null), context.getString(R.string.file_path_catalogs)).absolutePath
                    file.name.contains("midi") -> File(context.getExternalFilesDir(null), context.getString(R.string.file_path_midi)).absolutePath
                    else -> File(context.getExternalFilesDir(null), "others").absolutePath
                }
                FileManagerService.startActionUnzipFile(context, destination, zipFile)
            }

        }

        return Observable.create { emitter ->
            val handler = Handler(Looper.getMainLooper())
            val resultReceiver = object : ResultReceiver(handler) {

            }
        }

    }
}