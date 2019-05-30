package com.techbeloved.hymnbook.data

import android.support.v4.media.session.PlaybackStateCompat
import io.reactivex.Observable

interface PlayerPreferences {
    fun playbackRate(): Observable<Float>

    fun savePlaybackRate(rate: Float)

    fun repeatMode(): Observable<Int>

    fun saveRepeatMode(@PlaybackStateCompat.RepeatMode repeatMode: Int)
}