package com.techbeloved.hymnbook.data.repo

import io.reactivex.Observable

interface OnlineRepo {
    fun getAllHymns(): Observable<List<OnlineHymn>> = Observable.empty()
    fun getHymnById(id: Int): Observable<OnlineHymn> = Observable.empty()
    fun getLatestCatalogUrl(): Observable<String> = Observable.empty()
}

data class OnlineHymn( val id: Int, val title: String, val sheetMusicUrl: String = "", val isDownloaded: Boolean = false)
