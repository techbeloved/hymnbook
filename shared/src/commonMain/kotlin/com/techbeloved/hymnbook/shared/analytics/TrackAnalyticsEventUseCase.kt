package com.techbeloved.hymnbook.shared.analytics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class TrackAnalyticsEventUseCase @Inject constructor(
    private val analytics: AppAnalytics
) {

    suspend operator fun invoke(event: AnalyticsEvent) = withContext(Dispatchers.IO) {
        analytics.track(bundle = TrackingBundle(event = event.tracking, params = event.params))
    }
}
