package com.techbeloved.hymnbook.data

import io.reactivex.Observable
import io.reactivex.Single

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
     * Returns the currently saved midi archive version. Can be used to check with online version to determine if new download is required
     */
    fun midiArchiveVersion(): Single<Int>

    /**
     * Use to save the latest midi archive version
     */
    fun midiArchiveVersion(version: Int)

}