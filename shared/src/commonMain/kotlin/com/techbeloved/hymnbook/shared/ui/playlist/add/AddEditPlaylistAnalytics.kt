package com.techbeloved.hymnbook.shared.ui.playlist.add

import com.techbeloved.hymnbook.shared.analytics.TrackingName
import com.techbeloved.hymnbook.shared.analytics.TrackingParam
import com.techbeloved.hymnbook.shared.analytics.screenView

internal object AddEditPlaylistAnalytics {
    val screenName = TrackingName("add_edit_playlist_screen")

    fun screenView(isEdit: Boolean) = screenView(
        name = screenName,
        screenClass = "AddEditPlaylistDialog",
        params = mapOf(TrackingParam.ItemCategory to if (isEdit) "Edit" else "New"),
    )
}
