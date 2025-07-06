package com.techbeloved.hymnbook.shared.analytics

internal actual fun analyticsProvider(): AppAnalytics = object : AppAnalytics {
    override fun track(bundle: TrackingBundle) {
        println("Tracking: $bundle")
    }
}
