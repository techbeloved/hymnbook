package com.techbeloved.hymnbook.data

import android.content.res.Resources
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.R
import io.reactivex.Observable

class SharedPreferencesRepoImp(private val rxPreferences: RxSharedPreferences,
                               private val resources: Resources) : SharedPreferencesRepo {
    override fun isFirstStart(): Observable<Boolean> {
        val firstStartPref = rxPreferences.getBoolean(resources.getString(R.string.pref_key_first_start))
        return firstStartPref.asObservable()
    }

    override fun setFirstStart(firstStart: Boolean) {
        val firstStartPref = rxPreferences.getBoolean(resources.getString(R.string.pref_key_first_start))
        firstStartPref.set(firstStart)
    }

    override fun detailFontSize(): Observable<Float> {
        val fontSizePref = rxPreferences.getFloat(resources.getString(R.string.pref_key_detail_font_size))
        return fontSizePref.asObservable()
    }

    override fun updateDetailFontSize(newSize: Float) {
        val fontSizePref = rxPreferences.getFloat(resources.getString(R.string.pref_key_detail_font_size))
        fontSizePref.set(newSize)
    }

    override fun isNightModeActive(): Observable<Boolean> {
        val nightModePref = rxPreferences.getBoolean(resources.getString(R.string.pref_key_enable_night_mode))
        return nightModePref.asObservable()
    }

    override fun setNightModeActive(value: Boolean) {
        val nightModePref = rxPreferences.getBoolean(resources.getString(R.string.pref_key_enable_night_mode))
        nightModePref.set(value)
    }

    override fun midiFilesReady(): Observable<Boolean> {
        val midiFilesReadyPref = rxPreferences.getBoolean(resources.getString(R.string.pref_key_hymn_midi_files_ready))
        return midiFilesReadyPref.asObservable()
    }

    override fun updateMidiFilesReady(value: Boolean) {
        val midiFilesReadyPref = rxPreferences.getBoolean(resources.getString(R.string.pref_key_hymn_midi_files_ready))
        midiFilesReadyPref.set(value)
    }
}