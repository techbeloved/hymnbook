package com.techbeloved.hymnbook.data.download

/**
 * This class serves as a basic download manager. It can take urls and download them or hymn ids
 */
interface Downloader {
    fun enqueueDownloadForHymn(hymnId: Int)

    /**
     * Use to manually cancel any pending downloads. For example,
     *      when leaving the app in activity onDestroy to avoid memory leaks
     */
    fun clear()
}