package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Observable

interface OnlineRepo {
    fun getAllHymns(): Observable<List<OnlineHymn>> = Observable.empty()
    fun getHymnById(id: Int): Observable<OnlineHymn> = Observable.empty()
    fun getLatestCatalogUrl(): Observable<String> = Observable.empty()
    fun hymnIds(@SortBy orderBy: Int = BY_NUMBER): Observable<List<Int>>
}

data class OnlineHymn( val id: Int, val title: String, val sheetMusicUrl: String = "", val isDownloaded: Boolean = false)
