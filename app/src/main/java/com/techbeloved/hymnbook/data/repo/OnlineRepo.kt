package com.techbeloved.hymnbook.data.repo

import androidx.annotation.Keep
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Observable

interface OnlineRepo {
    /**
     * Returns all wrrcm hymns from the online repository (firebase)
     */
    fun getAllHymns(): Observable<List<OnlineHymn>> = Observable.empty()

    /**
     * Gets a wccrm hymn from online repository given by the [id]
     * @param id: Id or hymn number of hymn
     */
    fun getHymnById(id: Int): Observable<OnlineHymn> = Observable.empty()

    /**
     * Retrieves just the hymn ids for the hymns saved online. (Especially those with music sheet)
     */
    fun hymnIds(@SortBy orderBy: Int = BY_NUMBER): Observable<List<Int>>

    /**
     * Retrieves the latest midi archive from the online repo (firebase). This is used to get the link so download is made
     */
    fun latestMidiArchive(): Observable<OnlineMidi>
}

@Keep
data class OnlineHymn(val id: Int, val title: String, val sheetMusicUrl: String = "", val isDownloaded: Boolean = false)

/**
 * Model for passing online midi archive information from database
 */
@Keep
data class OnlineMidi(var id: String = "", var url: String = "", var version: Int = 0)
