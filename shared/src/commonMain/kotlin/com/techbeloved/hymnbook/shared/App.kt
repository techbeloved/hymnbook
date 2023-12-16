package com.techbeloved.hymnbook.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material.icons.twotone.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.techbeloved.hymnbook.shared.icons.IconPack
import com.techbeloved.hymnbook.shared.icons.iconpack.Home
import com.techbeloved.hymnbook.shared.model.HomeNavItem
import com.techbeloved.hymnbook.shared.theme.AppTheme
import com.techbeloved.hymnbook.shared.ui.navgraph.HomeScreen
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AppTheme {
        Navigator(screen = HomeScreen) { navigator ->
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Hymns") })
                },
                bottomBar = {
                    val (navItemSelectedIndex, setNavItemSelectedIndex) = rememberSaveable { mutableStateOf(0) }
                    NavigationBar(modifier = Modifier.fillMaxWidth()) {
                        bottomNavItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = index == navItemSelectedIndex,
                                onClick = {
                                    if (index != navItemSelectedIndex) {
                                        // onNavItemSelected(item)
                                        setNavItemSelectedIndex(index)
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(text = item.title) }
                            )
                        }
                    }
                },
            ) { paddingValues ->
                Surface(
                    Modifier.padding(paddingValues)
                        .fillMaxSize(),
                ) {
                    CurrentScreen()
                }
            }
        }
    }
}


private val bottomNavItems = persistentListOf(
    HomeNavItem("Home", IconPack.Home),
    HomeNavItem("Topics", Icons.Rounded.ThumbUp),
    HomeNavItem("Playlists", Icons.TwoTone.List),
)