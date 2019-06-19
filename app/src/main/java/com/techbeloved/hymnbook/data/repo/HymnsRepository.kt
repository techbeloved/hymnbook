package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.*
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Flowable
import io.reactivex.Observable

interface HymnsRepository {
    /**
     * Loads all hymn titles for given topicId if any. If no topic id is given (-1), then load all hymns
     */
    fun loadHymnTitles(@SortBy sortBy: Int, topicId: Int = 0): Flowable<List<HymnTitle>>
    fun getHymnDetailByNumber(hymnNo: Int): Flowable<HymnDetail>

    /**
     * Load hymn numbers for given topicId. If topic id is not specified, then load all hymn numbers
     */
    fun loadHymnIndices(sortBy: Int, topicId: Int = 0): Flowable<List<Int>>
    fun getHymnById(hymnNo: Int): Flowable<Hymn>
    fun searchHymns(searchTerm: String): Flowable<List<SearchResult>>
    fun loadHymnTitlesForIndices(indices: List<Int>, sortBy: Int): Observable<List<HymnTitle>>
    fun updateHymnDownloadProgress(hymnId: Int, progress: Int, downloadStatus: Int = DOWNLOAD_IN_PROGRESS)

    /**
     * Update the hymn download status such as on successful downloda
     */
    fun updateHymnDownloadStatus(hymnId: Int, progress: Int, downloadStatus: Int, remoteUri: String?, localUri: String?)

    fun updateHymnMidiPath(hymnId: Int, midiPath: String)
    /**
     * Retrieves all topics from database
     */
    fun loadAllTopics(): Observable<List<Topic>>
}
