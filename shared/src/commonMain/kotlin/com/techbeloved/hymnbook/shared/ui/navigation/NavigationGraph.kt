package com.techbeloved.hymnbook.shared.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techbeloved.hymnbook.shared.ui.detail.SongDetailScreen
import com.techbeloved.hymnbook.shared.ui.search.SearchScreen

internal fun NavGraphBuilder.addNavigationRoutes() {
    composable<SongDetailScreen> {
        SongDetailScreen()
    }

    composable<SearchScreen> {
        SearchScreen()
    }
}
