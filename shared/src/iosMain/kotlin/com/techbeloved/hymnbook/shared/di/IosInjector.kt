package com.techbeloved.hymnbook.shared.di

import com.techbeloved.hymnbook.shared.SwiftInterop
import com.techbeloved.hymnbook.shared.analytics.AppAnalytics

public object IosInjector {

    private var appAnalytics: AppAnalytics? = null

    private var _swiftInterop: SwiftInterop? = null

    internal val analytics
        get() = checkNotNull(appAnalytics) { "AppAnalytics not initialized" }

    internal val swiftInterop
        get() = checkNotNull(_swiftInterop) { "SwiftInterop not initialized" }

    public fun setAnalytics(appAnalytics: AppAnalytics) {
        this.appAnalytics = appAnalytics
    }

    public fun setSwiftInterop(swiftInterop: SwiftInterop) {
        this._swiftInterop = swiftInterop

    }
}
