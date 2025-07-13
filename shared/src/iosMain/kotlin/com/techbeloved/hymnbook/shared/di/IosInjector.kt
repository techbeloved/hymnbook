package com.techbeloved.hymnbook.shared.di

import com.techbeloved.hymnbook.shared.analytics.AppAnalytics

public object IosInjector {

    private var appAnalytics: AppAnalytics? = null

    internal val analytics
        get() = checkNotNull(appAnalytics) { "AppAnalytics not initialized" }

    public fun setAnalytics(appAnalytics: AppAnalytics) {
        this.appAnalytics = appAnalytics
    }
}
