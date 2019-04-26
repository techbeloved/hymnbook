package com.techbeloved.hymnbook.data

import com.techbeloved.hymnbook.data.model.CatalogStatus
import io.reactivex.Observable

interface SharedPreferencesRepo {
    /**
     * Flag that indicates the app is being used for the first time, could be after app reset
     */
    fun isFirstStart(): Observable<Boolean>

    /**
     * Update the first start flag
     */
    fun setFirstStart(firstStart: Boolean)

    /**
     * Returns the ids for currently queued downloads. This can be used to check that there's a download that is pending or should be processed
     */
    fun currentDownloadIds(): Observable<Set<String>>

    /**
     * Used to save a download id in the shared preferences
     */
    fun addCurrentDownloadId(currentId: String)

    /**
     * Returns the saved font size used to format the text in hymn detail view
     */
    fun detailFontSize(): Observable<Float>

    /**
     * Updates the font size in the shared preferences
     */
    fun updateDetailFontSize(newSize: Float)

    /**
     * Returns night mode preference.
     */
    fun isNightModeActive(): Observable<Boolean>

    /**
     * Updates the night mode preference
     */
    fun setNightModeActive(value: Boolean)

    /**
     * Checks that midi files are ready
     */
    fun midiFilesReady(): Observable<Boolean>

    /**
     * Updates the midi files ready preference
     */
    fun updateMidiFilesReady(value: Boolean)

    /**
     * Checks that hymns catalog status
     */

    fun hymnsCatalogDownloadStatus(): Observable<Int>

    /**
     * Updates the hymns catalog ready preference
     */
    fun updateHymnsCatalogStatus(@CatalogStatus value: Int)

    /**
     * Returns the download id for hymn catalog if any or -1
     */
    fun hymnsCatalogDownloadId(): Observable<Long>

    fun updateHymnsCatalogDownloadId(value: Long)

}