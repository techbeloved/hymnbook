package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen

internal object SearchScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { SearchScreenModel() }
        val state by screenModel.state.collectAsState()
        SearchUi(
            state = state,
            onSearch = { screenModel.onSearch() },
            onQueryChange = screenModel::onNewQuery,
            query = screenModel.searchQuery,
        )
    }
}
