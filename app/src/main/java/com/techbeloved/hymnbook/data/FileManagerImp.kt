package com.techbeloved.hymnbook.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.os.ResultReceiver
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.services.FileManagerService
import com.techbeloved.hymnbook.utils.Decompress
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File
import javax.inject.Inject

class FileManagerImp @Inject constructor(@ApplicationContext val context: Context) : FileManager {
    override fun unzipFile(source: String, destinationDir: String): Single<String> {
        return Single.create { emitter ->
            val destination = File(destinationDir)
            destination.mkdir()
            val decompress = Decompress(source, destination.absolutePath)
            try {
                decompress.unzip()
                if (!emitter.isDisposed) emitter.onSuccess(destinationDir)
            } catch (e: Exception) {
                emitter.tryOnError(Throwable("Error unzipping files!", e))
            }
        }
    }

    override fun deleteAllFilesInDir(dir: File): Observable<Long> {
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