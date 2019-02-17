package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.HymnTitle
import io.reactivex.Observable

class HymnsRepositoryImp: HymnsRepository {
    override fun loadHymnTitles(): Observable<List<HymnTitle>> {
        TODO("Not implemented")
    }

}