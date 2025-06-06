package com.techbeloved.hymnbook.shared.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.ui.detail.SongDetailScreen
import com.techbeloved.hymnbook.shared.ui.search.SearchScreen
import com.techbeloved.hymnbook.shared.ui.songs.FilteredSongsScreen

internal fun NavGraphBuilder.addNavigationRoutes(navController: NavHostController) {
    composable<SongDetailScreen> {
        SongDetailScreen()
    }

    composable<SearchScreen> {
        SearchScreen(onSongItemClicked = { song ->
            navController.navigate(
                SongDetailScreen(
                    initialSongId = song.id,
                    topics = SongFilter.NONE.topics,
                    songbooks = SongFilter.NONE.songbooks,
                    orderByTitle = SongFilter.NONE.orderByTitle,
                )
            )
        })
    }

    composable<FilteredSongsScreen> {
        val route = it.toRoute<FilteredSongsScreen>()
        FilteredSongsScreen(
            onSongItemClicked = { song ->
                navController.navigate(
                    SongDetailScreen(
                        initialSongId = song.id,
                        topics = route.topics,
                        songbooks = route.songbooks,
                        orderByTitle = route.orderByTitle,
                    )
                )
            }
        )
    }
}
