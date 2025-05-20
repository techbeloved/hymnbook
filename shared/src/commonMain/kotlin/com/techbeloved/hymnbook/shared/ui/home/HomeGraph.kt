package com.techbeloved.hymnbook.shared.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TravelExplore
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.techbeloved.hymnbook.shared.ui.appbar.HomeNavItem
import com.techbeloved.hymnbook.shared.ui.discover.DiscoverTabScreen
import com.techbeloved.hymnbook.shared.ui.more.MoreTabScreen
import com.techbeloved.hymnbook.shared.ui.playlist.PlayListTabScreen
import com.techbeloved.hymnbook.shared.ui.search.SearchScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

internal val navigationItems = persistentListOf(
    HomeNavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = TopLevelDestination.Home,
    ),
    HomeNavItem(
        label = "Discover",
        icon = Icons.Default.TravelExplore,
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

internal fun NavGraphBuilder.addHomeRoutes(navController: NavHostController) =
    navigation<TopLevelRoute>(startDestination = TopLevelDestination.Home) {
        composable<TopLevelDestination.Home> {
            HomeTabScreen(onOpenSearch = { navController.navigate(SearchScreen) })
        }

        composable<TopLevelDestination.Discover> {
            DiscoverTabScreen()
        }

        composable<TopLevelDestination.Playlists> {
            PlayListTabScreen()
        }

        composable<TopLevelDestination.More> {
            MoreTabScreen()
        }
    }

internal fun NavDestination.isATopLevelDestination(): Boolean = hasRoute<TopLevelDestination.Home>() ||
    hasRoute<TopLevelDestination.Playlists>() ||
    hasRoute<TopLevelDestination.Discover>() ||
    hasRoute<TopLevelDestination.More>()
