package com.techbeloved.hymnbook.data

import android.content.res.Resources
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.R
import io.reactivex.Observable

class PlayerPreferencesImp(private val rxPreferences: RxSharedPreferences,
                           private val resources: Resources) : PlayerPreferences {

    override fun playbackRate(): Observable<Float> {
        val ratePref = rxPreferences.getFloat(resources.getString(R.string.pref_key_playback_rate), 1.0f)
        return ratePref.asObservable()
    }

    override fun savePlaybackRate(rate: Float) {
        val ratePref = rxPreferences.getFloat(resources.getString(R.string.pref_key_playback_rate))
        ratePref.set(rate)
    }

    override fun repeatMode(): Observable<Int> {
        val repeatModePref = rxPreferences.getInteger(resources.getString(R.string.pref_key_repeat_mode))
        return repeatModePref.asObservable()
    }

    override fun saveRepeatMode(repeatMode: Int) {
        val repeatModePref = rxPreferences.getInteger(resources.getString(R.string.pref_key_repeat_mode))
        repeatModePref.set(repeatMode)
    }
}