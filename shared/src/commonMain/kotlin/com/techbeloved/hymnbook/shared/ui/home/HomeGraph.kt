package com.techbeloved.hymnbook.shared.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Topic
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.techbeloved.hymnbook.TopicEntity
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem
import com.techbeloved.hymnbook.shared.ui.appbar.HomeNavItem
import com.techbeloved.hymnbook.shared.ui.detail.SongDetailScreen
import com.techbeloved.hymnbook.shared.ui.discover.DiscoverTabScreen
import com.techbeloved.hymnbook.shared.ui.more.MoreTabScreen
import com.techbeloved.hymnbook.shared.ui.playlist.PlayListTabScreen
import com.techbeloved.hymnbook.shared.ui.playlist.add.AddEditPlaylistDialog
import com.techbeloved.hymnbook.shared.ui.search.SearchScreen
import com.techbeloved.hymnbook.shared.ui.songs.FilteredSongsScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

internal val navigationItems = persistentListOf(
    HomeNavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = TopLevelDestination.Home,
    ),
    HomeNavItem(
        label = "Topics",
        icon = Icons.Default.Topic,
        route = TopLevelDestination.Discover,
    ),
    HomeNavItem(
        label = "Playlists",
        icon = Icons.AutoMirrored.Filled.PlaylistPlay,
        route = TopLevelDestination.Playlists,
    ),
    HomeNavItem(
        label = "More",
        icon = Icons.Default.Menu,
        route = TopLevelDestination.More,
    ),
)

@Serializable
internal data object TopLevelRoute

internal sealed interface TopLevelDestination {
    @Serializable
    data object Home : TopLevelDestination

    @Serializable
    data object Discover : TopLevelDestination

    @Serializable
    data object Playlists : TopLevelDestination

    @Serializable
    data object More : TopLevelDestination
}

internal fun NavGraphBuilder.addHomeRoutes(
    navController: NavHostController
) = navigation<TopLevelRoute>(startDestination = TopLevelDestination.Home) {
    composable<TopLevelDestination.Home> {
        HomeTabScreen(
            onOpenSearch = { navController.navigate(SearchScreen) },
            onSongItemClicked = { song ->
                navController.navigate(
                    SongDetailScreen(
                        initialSongId = song.id,
                        topics = SongFilter.NONE.topics,
                        songbooks = song.songbook?.let { listOf(it) }
                            ?: SongFilter.NONE.songbooks,
                        orderByTitle = SongFilter.NONE.orderByTitle,
                        playlistIds = SongFilter.NONE.playlistIds,
                    )
                )
            },
        )
    }

    composable<TopLevelDestination.Discover> {
        DiscoverTabScreen { topic ->
            navController.navigate(topic.toSongsDestination())
        }
    }

    composable<TopLevelDestination.Playlists> {
        PlayListTabScreen(
            onAddPlaylistClick = {
                navController.navigate(AddEditPlaylistDialog(playlistId = null, songId = null))
            },
            onOpenPlaylistDetail = { item ->
                navController.navigate(item.toSongsDestination())
            },
        )
    }

    composable<TopLevelDestination.More> {
        MoreTabScreen()
    }
}

private fun TopicEntity.toSongsDestination() = FilteredSongsScreen(
    topics = listOf(name),
    songbooks = SongFilter.NONE.songbooks,
    orderByTitle = SongFilter.NONE.orderByTitle,
    playlistIds = SongFilter.NONE.playlistIds,
    title = name,
)

// This is temporary until the playlist detail screen is implemented
private fun PlaylistItem.toSongsDestination() = FilteredSongsScreen(
    topics = SongFilter.NONE.topics,
    songbooks = SongFilter.NONE.songbooks,
    orderByTitle = SongFilter.NONE.orderByTitle,
    playlistIds = listOf(id),
    title = name,
)

internal fun NavDestination.isATopLevelDestination(): Boolean =
    hasRoute<TopLevelDestination.Home>() ||
            hasRoute<TopLevelDestination.Playlists>() ||
            hasRoute<TopLevelDestination.Discover>() ||
            hasRoute<TopLevelDestination.More>()
