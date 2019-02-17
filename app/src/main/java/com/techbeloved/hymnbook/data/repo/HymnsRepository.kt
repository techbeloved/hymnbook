package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.HymnTitle
import io.reactivex.Observable

interface HymnsRepository {
    fun loadHymnTitles(): Observable<List<HymnTitle>>

}
