package com.techbeloved.hymnbook.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.techbeloved.hymnbook.shared.ui.navgraph.HomeScreen
import com.techbeloved.hymnbook.shared.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun App() {
    AppTheme {
        Navigator(screen = HomeScreen) { navigator ->
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Hymns") })
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
