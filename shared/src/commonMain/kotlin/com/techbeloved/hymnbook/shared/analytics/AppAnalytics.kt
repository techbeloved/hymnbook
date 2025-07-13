package com.techbeloved.hymnbook.shared.analytics

public interface AppAnalytics {
    public fun track(bundle: TrackingBundle)

    public fun setDefaultParams(params: Map<String, String>)
}
