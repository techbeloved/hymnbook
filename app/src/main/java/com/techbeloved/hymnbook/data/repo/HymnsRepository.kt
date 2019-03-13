package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.*
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Flowable

interface HymnsRepository {
    fun loadHymnTitles(@SortBy sortBy: Int): Flowable<List<HymnTitle>>
    fun getHymnDetailByNumber(hymnNo: Int): Flowable<HymnDetail>
    fun loadHymnIndices(sortBy: Int): Flowable<List<Int>>
    fun getHymnById(hymnNo: Int): Flowable<Hymn>
    fun searchHymns(searchTerm: String): Flowable<List<SearchResult>>
}
