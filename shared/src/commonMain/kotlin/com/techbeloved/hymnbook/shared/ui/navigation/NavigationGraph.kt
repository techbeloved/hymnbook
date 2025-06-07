package com.techbeloved.hymnbook.shared.ui.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.ui.detail.SongDetailScreen
import com.techbeloved.hymnbook.shared.ui.playlist.add.AddEditPlaylistDialog
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

    dialog<AddEditPlaylistDialog>(dialogProperties = DialogProperties()) {
        // Set dialog scrim to transparent
        AddEditPlaylistDialog(
            onDismiss = {
                navController.popBackStack()
            }
        )
    }
}
