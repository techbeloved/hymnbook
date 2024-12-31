package com.techbeloved.hymnbook

import android.app.Application
import com.techbeloved.hymnbook.shared.di.AndroidInjector

class HymnApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidInjector.init(this)
    }
}
