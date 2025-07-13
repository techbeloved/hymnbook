package com.techbeloved.hymnbook.shared.ui.playlist.select

import com.techbeloved.hymnbook.shared.analytics.TrackingName
import com.techbeloved.hymnbook.shared.analytics.screenView

internal object AddSongToPlaylistAnalytics {
    val screenName = TrackingName("add_song_to_playlist_screen")

    fun screenView() = screenView(
        name = screenName,
        screenClass = "AddSongToPlaylistDialog",
    )
}
