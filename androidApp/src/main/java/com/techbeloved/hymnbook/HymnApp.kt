package com.techbeloved.hymnbook

import android.app.Application
import com.techbeloved.hymnbook.shared.appversion.AppVersion
import com.techbeloved.hymnbook.shared.di.AndroidInjector

class HymnApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidInjector.init(
            application = this,
            appVersion = AppVersion(
                name = BuildConfig.VERSION_NAME,
                code = BuildConfig.VERSION_CODE.toString(),
            )
        )
    }
}
