package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.*
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Flowable
import io.reactivex.Observable

interface HymnsRepository {
    fun loadHymnTitles(@SortBy sortBy: Int): Flowable<List<HymnTitle>>
    fun getHymnDetailByNumber(hymnNo: Int): Flowable<HymnDetail>
    fun loadHymnIndices(sortBy: Int): Flowable<List<Int>>
    fun getHymnById(hymnNo: Int): Flowable<Hymn>
    fun searchHymns(searchTerm: String): Flowable<List<SearchResult>>
    fun loadHymnTitlesForIndices(indices: List<Int>): Observable<List<HymnTitle>>
    fun updateHymnDownloadProgress(hymnId: Int, progress: Int, downloadStatus: Int = DOWNLOAD_IN_PROGRESS)

    /**
     * Update the hymn download status such as on successful downloda
     */
    fun updateHymnDownloadStatus(hymnId: Int, progress: Int, downloadStatus: Int, remoteUri: String?, localUri: String?)
}
