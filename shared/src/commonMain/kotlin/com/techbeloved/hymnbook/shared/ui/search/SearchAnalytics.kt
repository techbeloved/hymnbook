package com.techbeloved.hymnbook.shared.ui.search

import com.techbeloved.hymnbook.shared.analytics.TrackingName
import com.techbeloved.hymnbook.shared.analytics.actionSearch
import com.techbeloved.hymnbook.shared.analytics.screenView

internal object SearchAnalytics {
    val screenName = TrackingName("search_screen")

    val screenView = screenView(
        name = screenName,
        screenClass = "SearchScreen",
    )

    fun actionSearch(query: String) = actionSearch(
        context = screenName,
        query = query,
    )
}
