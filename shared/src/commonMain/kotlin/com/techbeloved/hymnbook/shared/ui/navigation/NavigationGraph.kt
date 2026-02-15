package com.techbeloved.hymnbook.shared.ui.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.ui.detail.SongDetailScreen
import com.techbeloved.hymnbook.shared.ui.more.about.AboutScreen
import com.techbeloved.hymnbook.shared.ui.more.about.OpenSourceLicencesScreen
import com.techbeloved.hymnbook.shared.ui.playlist.add.AddEditPlaylistDialog
import com.techbeloved.hymnbook.shared.ui.playlist.select.AddSongToPlaylistDialog
import com.techbeloved.hymnbook.shared.ui.search.SearchScreen
import com.techbeloved.hymnbook.shared.ui.songs.FilteredSongsScreen
import com.techbeloved.hymnbook.shared.ui.soundfonts.SoundFontSettingsScreen

internal fun NavGraphBuilder.addNavigationRoutes(
    navController: NavHostController,
    onShowSnackbarMessage: (message: String) -> Unit,
) {
    songDetailDestination(navController)
    searchScreenDestination(navController)
    filteredSongsScreenDestination(navController)
    addEditPlaylistDialogDestination(navController, onShowSnackbarMessage)
    addSongToPlaylistDialogDestination(navController, onShowSnackbarMessage)
    aboutScreenDestination(navController)

    addSoundFontSettingsDestination(navController)

    composable<OpenSourceLicencesScreen> {
        OpenSourceLicencesScreen()
    }
}

private fun NavGraphBuilder.aboutScreenDestination(navController: NavHostController) {
    composable<AboutScreen> {
        AboutScreen(
            onPrivacyPolicyClick = {},
            onOpenSourceLicencesClick = { navController.navigate(OpenSourceLicencesScreen) },
            onTermsAndConditionsClick = {},
        )
    }
}

private fun NavGraphBuilder.songDetailDestination(navController: NavHostController) {
    composable<SongDetailScreen> {
        SongDetailScreen(
            onAddSongToPlaylist = { songId ->
                navController.navigate(AddSongToPlaylistDialog(songId))
            },
            onOpenSearch = {
                navController.navigate(SearchScreen())
            },
            onShowSoundFontSettings = {
                navController.navigate(SoundFontSettingsScreen)
            }
        )
    }
}

private fun NavGraphBuilder.searchScreenDestination(navController: NavHostController) {
    composable<SearchScreen> {
        val args = it.toRoute<SearchScreen>()
        SearchScreen(
            onSongItemClicked = { song ->
                navController.navigate(
                    SongDetailScreen(
                        initialSongId = song.id,
                        topics = SongFilter.NONE.topics,
                        songbooks = song.songbook?.let { listOf(it) } ?: SongFilter.NONE.songbooks,
                        orderByTitle = SongFilter.NONE.orderByTitle,
                        playlistIds = SongFilter.NONE.playlistIds,
                    )
                )
            },
            isSpeedDial = args.isSpeedDial,
        )
    }
}

private fun NavGraphBuilder.filteredSongsScreenDestination(navController: NavHostController) {
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
                        playlistIds = route.playlistIds,
                    )
                )
            }
        )
    }
}

private fun NavGraphBuilder.addEditPlaylistDialogDestination(
    navController: NavHostController,
    onShowSnackbarMessage: (String) -> Unit,
) {
    dialog<AddEditPlaylistDialog>(dialogProperties = DialogProperties()) {
        AddEditPlaylistDialog(
            onDismiss = { saved ->
                navController.popBackStack()
                if (saved != null && saved.songAdded) {
                    onShowSnackbarMessage("Song added to new playlist successfully")
                }
            }
        )
    }
}

private fun NavGraphBuilder.addSongToPlaylistDialogDestination(
    navController: NavHostController,
    onShowSnackbarMessage: (String) -> Unit,
) {
    dialog<AddSongToPlaylistDialog>(dialogProperties = DialogProperties()) { backstackEntry ->
        val args = backstackEntry.toRoute<AddSongToPlaylistDialog>()
        AddSongToPlaylistDialog(
            onDismiss = { successMessage ->
                navController.popBackStack()
                if (successMessage != null) {
                    onShowSnackbarMessage(successMessage)
                }
            },
            onCreateNewPlaylist = {
                navController.navigate(
                    AddEditPlaylistDialog(
                        songId = args.songId,
                        playlistId = null
                    ),
                ) {
                    launchSingleTop = true
                    popUpTo<AddSongToPlaylistDialog> {
                        inclusive = true
                    }
                }
            },
        )
    }
}

private fun NavGraphBuilder.addSoundFontSettingsDestination(
    navController: NavHostController,
) {
    dialog<SoundFontSettingsScreen>(dialogProperties = DialogProperties()) { backstackEntry ->
        SoundFontSettingsScreen(onDismiss = {
            navController.popBackStack()
        })
    }
}
