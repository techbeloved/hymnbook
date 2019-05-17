package com.techbeloved.hymnbook.data.model

sealed class CatalogDownloadStatus {

    /**
     * Catalog has been download and processed (unzipped)
     */
    object CatalogReady : CatalogDownloadStatus()

    /**
     * Downloaded but may not be processed yet
     */
    data class Downloaded(val downloadId: Long, val downloadPath: String) : CatalogDownloadStatus()

    /**
     * @param progress is percentage downloaded
     */
    data class DownloadInProgress(val downloadId: Long, val progress: Long = 0, val paused: Boolean = false) : CatalogDownloadStatus()

    /**
     * Download has been enqueued
     * @param downloadId id of the download
     */
    //data class DownloadEnqueued(val downloadId: Long): CatalogDownloadStatus()

    /**
     * There is some kind of failure
     */
    data class Failure(val error: Throwable) : CatalogDownloadStatus()

    //data class Paused(val reason: String) : CatalogDownloadStatus()

    /**
     * New update is available from the server
     */
    object UpdateAvailable : CatalogDownloadStatus()
}