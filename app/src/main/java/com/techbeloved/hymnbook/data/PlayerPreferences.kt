package com.techbeloved.hymnbook.data

import io.reactivex.Observable

interface PlayerPreferences {
    fun playbackRate(): Observable<Float>

    fun savePlaybackRate(rate: Float)

    fun repeatMode(): Observable<Int>

    fun saveRepeatMode(repeatMode: Int)
}