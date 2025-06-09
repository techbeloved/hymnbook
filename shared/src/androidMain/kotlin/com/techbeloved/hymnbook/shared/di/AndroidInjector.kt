package com.techbeloved.hymnbook.shared.di

import android.app.Application
import com.techbeloved.hymnbook.shared.appversion.AppVersion

public object AndroidInjector {

    internal var appVersion: AppVersion = AppVersion(name = "1.0", code = "1")
        private set
    private var app: Application? = null
    internal val application
        get() = checkNotNull(app) { "Application not initialized" }

    public fun init(application: Application, appVersion: AppVersion) {
        this.app = application
        this.appVersion = appVersion
    }
}
