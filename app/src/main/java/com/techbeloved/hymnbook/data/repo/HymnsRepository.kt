package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnTitle
import io.reactivex.Flowable

interface HymnsRepository {
    fun loadHymnTitles(): Flowable<List<HymnTitle>>
    fun getHymnDetailByNumber(hymnNo: Int): Flowable<HymnDetail>

}
