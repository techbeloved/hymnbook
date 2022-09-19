package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.DOWNLOAD_IN_PROGRESS
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.SearchResult
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.model.HymnNumber
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Completable
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
    fun loadHymnIndices(sortBy: Int, topicId: Int = 0): Flowable<List<HymnNumber>>
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

    fun getTopicById(topicId: Int): Observable<Topic>
    fun synchroniseOnlineMusic(onlineHymns: List<OnlineHymn>): Completable
}
