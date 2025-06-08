@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.songs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.listing.SongListingUi
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.serialization.Serializable

@Serializable
internal data class FilteredSongsScreen(
    val topics: List<String>,
    val songbooks: List<String>,
    val playlistIds: List<Long>,
    val orderByTitle: Boolean,
    val title: String = "",
) {
    val songFilter get() = SongFilter(
        topics = topics,
        songbooks = songbooks,
        playlistIds = playlistIds,
        orderByTitle = orderByTitle,
    )
}

@Composable
internal fun FilteredSongsScreen(
    onSongItemClicked: (SongTitle) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FilteredSongsViewModel = viewModel(factory = FilteredSongsViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    FilteredSongsUi(
        state = state,
        modifier = modifier.fillMaxSize(),
        onSongItemClicked = onSongItemClicked,
    )
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun FilteredSongsUi(
    state: FilteredSongsState,
    onSongItemClicked: (SongTitle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = remember { HazeState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                scrollBehaviour = scrollBehavior,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .5f),
                modifier = Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()),
                title = state.title,
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        SongListingUi(
            songItems = state.songs,
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize(),
            onSongItemClicked = onSongItemClicked,
        )
    }
}
