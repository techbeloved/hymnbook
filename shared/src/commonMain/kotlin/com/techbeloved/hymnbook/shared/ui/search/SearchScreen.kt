package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchScreen(val isSpeedDial: Boolean = false)

@Composable
internal fun SearchScreen(
    isSpeedDial: Boolean,
    onSongItemClicked: (SongTitle) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchScreenModel = viewModel(factory = SearchScreenModel.Factory),
) {
    LaunchedEffect(Unit) {
        viewModel.onScreenLoaded()
    }
    val state by viewModel.state.collectAsState()
    SearchUi(
        state = state,
        onSearch = { viewModel.onSearch() },
        onQueryChange = viewModel::onNewQuery,
        query = viewModel.searchQuery,
        modifier = modifier,
        onSongItemClicked = onSongItemClicked,
        onFilterBySongbook = viewModel::onFilterBySongbook,
        isSpeedDial = isSpeedDial,
    )
}
