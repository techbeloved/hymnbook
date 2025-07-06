package com.techbeloved.hymnbook.shared.di

import android.app.Application
import com.techbeloved.hymnbook.shared.analytics.AppAnalytics
import com.techbeloved.hymnbook.shared.appversion.AppVersion

public object AndroidInjector {

    private var appAnalytics: AppAnalytics? = null
    internal val analytics
        get() = checkNotNull(appAnalytics) { "AppAnalytics not initialized" }

    internal var appVersion: AppVersion = AppVersion(name = "1.0", code = "1")
        private set
    private var app: Application? = null
    internal val application
        get() = checkNotNull(app) { "Application not initialized" }

    public fun init(
        application: Application,
        appVersion: AppVersion,
        appAnalytics: AppAnalytics? = null,
    ) {
        this.appAnalytics = appAnalytics
        this.app = application
        this.appVersion = appVersion
    }
}
