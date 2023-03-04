package com.techbeloved.hymnbook.data

import android.content.Context
import com.techbeloved.hymnbook.utils.Decompress
import dagger.hilt.android.qualifiers.ApplicationContext
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
}
