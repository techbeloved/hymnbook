package com.techbeloved.hymnbook.data.download

import android.app.DownloadManager
import io.reactivex.Observable

interface DownloadService {

    /**
     * Puts a download request on line for to be handled by the [DownloadManager].
     * @param sourceUrl is the url of the file you want to download
     * @param destinationSubPath is the destination dir you want to put it. Default is downloads in the apps private files dir
     * @param showNotification whether to show notification of the download progress
     *
     * @return download id to identify this particular download
     */
    fun enqueueDownload(sourceUrl: String, destinationSubPath: String = "downloads/", showNotification: Boolean = true): Observable<Long>

    /**
     * Returns the progress of the download given by the id
     */
    fun downloadProgress(downloadId: Long): Observable<Long>

    /**
     * Returns the download size of the particular download in bytes
     */
    fun downloadSize(downloadId: Long): Observable<Long>

    /**
     * Delete a particular download identified by the id, also cancels the task if it is an ongoing download
     * @param downloadId is the id to identify the particular download
     */
    fun deleteDownload(downloadId: Long)

    /**
     * Cancel all current download tasks and delete already downloaded files
     */
    fun deleteAllDownloads(vararg ids: Long)

    fun downloadStatus(downloadId: Long): Observable<out DownloadStatus>

}