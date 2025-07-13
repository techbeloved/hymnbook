package com.techbeloved.hymnbook.shared.ui.songs

import com.techbeloved.hymnbook.shared.analytics.TrackingName
import com.techbeloved.hymnbook.shared.analytics.TrackingParam
import com.techbeloved.hymnbook.shared.analytics.screenView
import com.techbeloved.hymnbook.shared.model.SongFilter

internal object FilteredSongsAnalytics {
    val screenName = TrackingName("filtered_songs_screen")

    fun screenView(songFilter: SongFilter) = screenView(
        name = screenName,
        screenClass = "FilteredSongsScreen",
        params = mapOf(
            TrackingParam.ItemCategory to songFilter.getCategory(),
        ),
    )

    private fun SongFilter.getCategory() = when {
        byTopicsAndSongbooks -> "songbooks: ${songbooks.joinToString()} - topics: ${topics.joinToString()}"
        byPlaylists -> "by_playlist"
        bySongbooks -> "by_songbook: ${songbooks.joinToString()}"
        byTopicsOnly -> "by_topic:  ${topics.joinToString()}"
        else -> "all"
    }
}
