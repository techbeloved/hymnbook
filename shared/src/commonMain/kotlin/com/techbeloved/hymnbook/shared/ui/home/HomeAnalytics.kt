package com.techbeloved.hymnbook.shared.ui.home

import com.techbeloved.hymnbook.shared.analytics.TrackingName
import com.techbeloved.hymnbook.shared.analytics.actionUpdateSettings
import com.techbeloved.hymnbook.shared.analytics.screenView
import com.techbeloved.hymnbook.shared.songbooks.SongbookPreferenceKey

internal object HomeAnalytics {
    val screenName = TrackingName("home_screen")

    val screenView = screenView(
        name = screenName,
        screenClass = "HomeTabScreen",
        params = mapOf(),
    )

    fun actionSelectSongbook(value: String) = actionUpdateSettings(
        settingsId = SongbookPreferenceKey.key.name,
        value = value,
    )

    fun actionUpdateSortBy(value: String) = actionUpdateSettings(
        settingsId = "songs.sort_by",
        value = value,
    )

}
