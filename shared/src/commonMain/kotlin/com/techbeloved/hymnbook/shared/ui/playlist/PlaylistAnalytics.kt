package com.techbeloved.hymnbook.shared.ui.playlist

import com.techbeloved.hymnbook.shared.analytics.TrackingName
import com.techbeloved.hymnbook.shared.analytics.screenView

internal object PlaylistAnalytics {
    val screenName = TrackingName("playlists_screen")

    fun screenView() = screenView(
        name = screenName,
        screenClass = "PlaylistTabScreen",
    )

}
