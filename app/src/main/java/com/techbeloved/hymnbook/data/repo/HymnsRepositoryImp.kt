package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import io.reactivex.Flowable

class HymnsRepositoryImp(private val hymnDatabase: HymnsDatabase): HymnsRepository {
    override fun loadHymnTitles(): Flowable<List<HymnTitle>> {
        return hymnDatabase.hymnDao().getAllHymnTitles()
    }

}