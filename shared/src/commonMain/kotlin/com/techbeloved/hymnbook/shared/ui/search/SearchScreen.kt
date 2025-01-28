package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.techbeloved.hymnbook.shared.di.appComponent

internal object SearchScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { appComponent.searchScreenModel() }
        val state by screenModel.state.collectAsState()
        SearchUi(
            state = state,
            onSearch = { screenModel.onSearch() },
            onQueryChange = screenModel::onNewQuery,
            query = screenModel.searchQuery,
        )
    }
}
