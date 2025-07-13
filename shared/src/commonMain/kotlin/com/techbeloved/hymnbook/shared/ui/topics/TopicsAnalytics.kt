package com.techbeloved.hymnbook.shared.ui.topics

import com.techbeloved.hymnbook.shared.analytics.TrackingName
import com.techbeloved.hymnbook.shared.analytics.screenView

internal object TopicsAnalytics {
    val screenName = TrackingName("topics_screen")

    val screenView = screenView(
        name = screenName,
        screenClass = "TopicsScreen",
    )
}
