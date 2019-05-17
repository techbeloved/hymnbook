package com.techbeloved.hymnbook.data.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * Pending implementation (untested) of custom downloader that use the system download manager
 */
class DownloadServiceImp(val context: Context, val sharedPreferences: SharedPreferencesRepo, val downloadManager: DownloadManager) : DownloadService {

    private var downloadReceiverRegistered = false

    private var onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.i("Received download complete intent")
            val id: Long? = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            Timber.i("DownloadId: %s", id)
            id?.let {
                if (it >= 0) {
                    downloadStatus(it)
                            .subscribeWith(downloadPublishSubject)
                }
            }
        }
    }

    init {
    }

    override fun enqueueDownload(sourceUrl: String, destinationFileName: String, destinationSubPath: String, showNotification: Boolean): Observable<Long> {
        return Observable.create { emitter ->
            val request = DownloadManager.Request(Uri.parse(sourceUrl))
                    .setTitle(destinationFileName)
                    .setDescription("Downloading")
                    .setNotificationVisibility(if (showNotification) DownloadManager.Request.VISIBILITY_VISIBLE else DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalFilesDir(context, null, "$destinationSubPath/$destinationFileName")
                    .setAllowedOverMetered(true)

            val downloadId = downloadManager.enqueue(request)

            // Save download id in shared preferences
            sharedPreferences.addCurrentDownloadId(downloadId.toString())

            emitter.onNext(downloadId)
        }
    }

    override fun downloadProgress(downloadId: Long): Observable<Long> {
        return Observable.create { emitter ->
            val query: DownloadManager.Query = DownloadManager.Query()
                    .setFilterById(downloadId)

            val cursor = downloadManager.query(query)
            if (cursor.count > 0 && !cursor.isClosed) {
                try {
                    cursor.moveToFirst()
                    val downloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val totalSize = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val percentProgress = (downloaded / (totalSize.toFloat()) * 100).toLong()
                    if (!emitter.isDisposed) {
                        // Emit the percentage
                        emitter.onNext(percentProgress)
                    }
                } catch (e: Exception) {
                    emitter.tryOnError(e)
                }

            }
            if (!cursor.isClosed) {
                cursor.close()
            }

            emitter.setCancellable { if (!cursor.isClosed) cursor.close() }
        }
    }

    override fun downloadSize(downloadId: Long): Observable<Long> {
        return Observable.create { emitter ->
            val query: DownloadManager.Query = DownloadManager.Query()
                    .setFilterById(downloadId)

            val cursor = downloadManager.query(query)
            if (cursor.count > 0 && !cursor.isClosed) {
                try {
                    cursor.moveToFirst()
                    val totalSize = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    if (!emitter.isDisposed) {
                        // Emit the percentage
                        emitter.onNext(totalSize)
                    }
                } catch (e: Exception) {
                    emitter.tryOnError(e)
                }

            }
            if (!cursor.isClosed) {
                cursor.close()
            }

            emitter.setCancellable { if (!cursor.isClosed) cursor.close() }
        }
    }

    override fun deleteDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
    }

    override fun deleteAllDownloads(vararg ids: Long) {
        downloadManager.remove(*ids)
    }

    override fun downloadStatus(downloadId: Long): Observable<out DownloadStatus> {
        return Observable.create { emitter ->
            val query: DownloadManager.Query = DownloadManager.Query()
                    .setFilterById(downloadId)

            val cursor = downloadManager.query(query)
            if (cursor.count > 0 && !cursor.isClosed) {
                try {
                    cursor.moveToFirst()
                    val downloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val totalSize = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val percentProgress = (downloaded / (totalSize.toFloat()) * 100).toLong()
                    val downloadPath = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                    if (!emitter.isDisposed) {

                        when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_SUCCESSFUL -> emitter.onNext(DownloadStatus.Success(downloadId, downloadPath))
                            DownloadManager.STATUS_RUNNING -> emitter.onNext(DownloadStatus.Running(percentProgress))
                            DownloadManager.STATUS_PAUSED -> emitter.onNext(DownloadStatus.Paused(percentProgress))
                            DownloadManager.STATUS_PENDING -> emitter.onNext(DownloadStatus.Pending)
                            DownloadManager.STATUS_FAILED -> {
                                val failureReason = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                                emitter.onNext(DownloadStatus.Failure(failureReason))
                            }
                        }
                    }
                } catch (e: Exception) {
                    emitter.tryOnError(e)
                }

            }
            if (!cursor.isClosed) {
                cursor.close()
            }
            emitter.setCancellable { if (!cursor.isClosed) cursor.close() }
        }
    }

    private val downloadPublishSubject: PublishSubject<DownloadStatus> = PublishSubject.create()
    override fun listenForCompletedDownloads(): Observable<out DownloadStatus.Success> {
        if (!downloadReceiverRegistered) {
            context.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            downloadReceiverRegistered = true
        }

        return downloadPublishSubject.ofType(DownloadStatus.Success::class.java)
                .hide()
    }

    override fun cancelDownloadReceivers() {
        context.unregisterReceiver(onDownloadComplete)
        downloadReceiverRegistered = false
    }

}