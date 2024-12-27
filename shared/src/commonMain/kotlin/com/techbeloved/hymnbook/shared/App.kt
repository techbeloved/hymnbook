package com.techbeloved.hymnbook.shared

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.techbeloved.hymnbook.shared.ui.home.HomeScreen
import com.techbeloved.hymnbook.shared.ui.theme.AppTheme

@Composable
public fun App() {
    AppTheme {
        Navigator(screen = HomeScreen)
    }
}
