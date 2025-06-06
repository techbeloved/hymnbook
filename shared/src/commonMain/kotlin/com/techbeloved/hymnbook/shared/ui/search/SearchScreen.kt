package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.serialization.Serializable

@Serializable
internal object SearchScreen

@Composable
internal fun SearchScreen(
    onSongItemClicked: (SongTitle) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchScreenModel = viewModel(factory = SearchScreenModel.Factory),
) {
    val state by viewModel.state.collectAsState()
    SearchUi(
        state = state,
        onSearch = { viewModel.onSearch() },
        onQueryChange = viewModel::onNewQuery,
        query = viewModel.searchQuery,
        modifier = modifier,
        onSongItemClicked = onSongItemClicked,
    )
}
