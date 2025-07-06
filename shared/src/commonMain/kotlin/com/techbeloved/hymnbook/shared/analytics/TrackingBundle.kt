package com.techbeloved.hymnbook.shared.analytics

public data class TrackingBundle(
    val event: TrackingEvent,
    val params: Map<TrackingParam, String>,
)
