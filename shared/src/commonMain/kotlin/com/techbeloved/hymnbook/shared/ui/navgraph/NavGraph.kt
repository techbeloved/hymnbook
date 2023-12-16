package com.techbeloved.hymnbook.shared.ui.navgraph

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.techbeloved.hymnbook.shared.ui.home.HomeScreen
import com.techbeloved.hymnbook.shared.ui.home.HomeScreenModel

internal object HomeScreen : Screen {
    @Composable
    override fun Content() {
        HomeScreen(screenModel = rememberScreenModel { HomeScreenModel() })
    }

}
