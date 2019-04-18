package com.techbeloved.hymnbook.data.repo

import io.reactivex.Observable

interface OnlineRepo {
    fun getAllHymns(): Observable<List<OnlineHymn>> = Observable.empty()
}

data class OnlineHymn( val id: Int, val title: String)
