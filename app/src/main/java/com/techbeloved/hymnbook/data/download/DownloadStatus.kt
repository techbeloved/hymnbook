package com.techbeloved.hymnbook.data.download

/**
 * Status of each download
 */
sealed class DownloadStatus {
    /**
     * Download failed and will not be retried
     */
    data class Failure(val reason: DownloadErrorReason) : DownloadStatus()

    /**
     * when the download has successfully completed.
     */
    object Success : DownloadStatus()

    /**
     * Download is ongoing
     * @param progress percentage progress
     */
    data class Running(val progress: Double) : DownloadStatus()

    /**
     * when the download is waiting to start.
     */
    object Pending : DownloadStatus()

    /**
     * when the download is waiting to retry or resume.
     */
    data class Paused(val reason: DownloadPauseReason) : DownloadStatus()
}

/**
 * Reason for download failure
 */
sealed class DownloadErrorReason {
    /**
     *  when some possibly transient error occurred but we can't resume the download.
     */
    object CannotResume : DownloadErrorReason() {
        override fun toString(): String {
            return "Cannot Resume"
        }
    }

    /**
     * when no external storage device was found.
     */
    object DeviceNotFound : DownloadErrorReason() {
        override fun toString(): String {
            return "External Storage Device Not Found"
        }
    }

    /**
     * when the requested destination file already exists (the download manager will not overwrite an existing file).
     */
    object FileAlreadyExists : DownloadErrorReason() {
        override fun toString(): String {
            return "File already exists"
        }
    }

    /**
     * when a storage issue arises which doesn't fit under any other error code.
     */
    object FileError : DownloadErrorReason() {
        override fun toString(): String {
            return "File Error"
        }
    }

    /**
     * when an error receiving or processing data occurred at the HTTP level.
     */
    object HttpDataError : DownloadErrorReason() {
        override fun toString(): String {
            return "Http Data Error"
        }
    }

    /**
     * when there was insufficient storage space.
     */
    object InsufficientSpace : DownloadErrorReason()

    /**
     * when there were too many redirects.
     */
    object TooManyRedirects : DownloadErrorReason()

    /**
     * when an HTTP code was received that download manager can't handle.
     */
    object UnhandledHttpCode : DownloadErrorReason()

    /**
     * when the download has completed with an error that doesn't fit under any other error code.
     */
    object Unknown : DownloadErrorReason()
}

/**
 * The various reasons for download pauses
 */
sealed class DownloadPauseReason {
    object QueuedForWifi : DownloadPauseReason()
    /**
     * when the download is paused for some other reason.
     */
    object Unknown : DownloadPauseReason()

    /**
     * when the download is waiting for network connectivity to proceed.
     */
    object WaitingForNetwork : DownloadPauseReason()

    /**
     * when the download is paused because some network error occurred and the download manager is waiting before retrying the request.
     */
    object WaitingToRetry : DownloadPauseReason()
}