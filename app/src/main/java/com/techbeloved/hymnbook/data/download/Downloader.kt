package com.techbeloved.hymnbook.data.download

import io.reactivex.Single
import java.io.File

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

    /**
     * Downloads archive from firebase storage.
     * @param archivePath: is the subpath of the archive on the firebase storage such as "tunes/wccrm/midi_archive/hymns.zip"
     */
    fun downloadFirebaseArchive(archivePath: String, destination: File): Single<String>
}