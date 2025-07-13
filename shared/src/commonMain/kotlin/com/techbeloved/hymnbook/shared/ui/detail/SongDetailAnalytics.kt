package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.analytics.TrackingName
import com.techbeloved.hymnbook.shared.analytics.TrackingParam
import com.techbeloved.hymnbook.shared.analytics.actionUpdateSettings
import com.techbeloved.hymnbook.shared.analytics.screenView
import com.techbeloved.hymnbook.shared.model.SongFilter

internal object SongDetailAnalytics {
    val screenName = TrackingName("song_detail_screen")
    fun screenView(
        songFilter: SongFilter,
    ) = screenView(
        name = screenName,
        screenClass = "SongDetailScreen",
        params = mapOf(
            TrackingParam.ItemCategory to songFilter.getCategory(),
        ),
    )

    fun actionToggleLooping(isLooping: Boolean) = actionUpdateSettings(
        settingsId = "audio.looping",
        value = isLooping.toString(),
    )

    fun actionChangeSpeed(speed: Int) = actionUpdateSettings(
        settingsId = "audio.speed",
        value = speed.toString(),
    )

    private fun SongFilter.getCategory() = when {
        byPlaylists -> "playlist"
        bySongbooks -> "songbook"
        byTopicsOnly -> "topic"
        else -> "all"
    }
}
