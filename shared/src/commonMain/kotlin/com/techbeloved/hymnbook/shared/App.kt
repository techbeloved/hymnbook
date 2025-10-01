package com.techbeloved.hymnbook.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.techbeloved.hymnbook.shared.songshare.SongShareHandler
import com.techbeloved.hymnbook.shared.ui.analytics.LogDefaultAnalytics
import com.techbeloved.hymnbook.shared.ui.appbar.BottomNavigationBar
import com.techbeloved.hymnbook.shared.ui.home.TopLevelDestination
import com.techbeloved.hymnbook.shared.ui.home.addHomeRoutes
import com.techbeloved.hymnbook.shared.ui.home.isATopLevelDestination
import com.techbeloved.hymnbook.shared.ui.home.navigationItems
import com.techbeloved.hymnbook.shared.ui.navigation.LocalNavController
import com.techbeloved.hymnbook.shared.ui.navigation.addNavigationRoutes
import com.techbeloved.hymnbook.shared.ui.theme.AppTheme
import kotlinx.coroutines.launch

internal const val AppHost = "app.watchmanmusic.com"

@Composable
public fun App() {
    LogDefaultAnalytics()
    AppTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val isBottomNavVisible by derivedStateOf { navBackStackEntry?.destination?.isATopLevelDestination() == true }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    isBottomNavVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                ) {
                    BottomNavigationBar(
                        items = navigationItems,
                        navController = navController,
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) {
            CompositionLocalProvider(LocalNavController provides navController) {
                NavHost(
                    navController = navController,
                    startDestination = TopLevelDestination.Home,
                    modifier = Modifier,
                ) {
                    addHomeRoutes(navController)

                    addNavigationRoutes(navController) { snackMessage ->
                        scope.launch { snackbarHostState.showSnackbar(snackMessage) }
                    }
                }
            }
            SongShareHandler(navController)
        }
    }
}
