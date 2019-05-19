package com.techbeloved.hymnbook.fakes

import android.content.res.Resources
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import io.reactivex.Observable
import io.reactivex.Single

class TestSharedPreferencesImp(private val rxPreferences: RxSharedPreferences, private val resources: Resources) : SharedPreferencesRepo {
    override fun isFirstStart(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setFirstStart(firstStart: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun detailFontSize(): Observable<Float> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateDetailFontSize(newSize: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isNightModeActive(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNightModeActive(value: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun midiFilesReady(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateMidiFilesReady(value: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun midiArchiveVersion(): Single<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun midiArchiveVersion(version: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}