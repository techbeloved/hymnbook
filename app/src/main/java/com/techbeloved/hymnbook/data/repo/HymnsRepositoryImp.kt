package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import io.reactivex.Flowable

class HymnsRepositoryImp(private val hymnDatabase: HymnsDatabase) : HymnsRepository {
    override fun getHymnDetailByNumber(hymnNo: Int): Flowable<HymnDetail> {
        return hymnDatabase.hymnDao().getHymnDetail(hymnNo)
    }

    override fun loadHymnTitles(): Flowable<List<HymnTitle>> {
        return hymnDatabase.hymnDao().getAllHymnTitles()
    }

}