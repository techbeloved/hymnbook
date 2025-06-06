package com.techbeloved.hymnbook.shared.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.listing.HymnListingUi
import com.techbeloved.hymnbook.shared.ui.search.HomeSearchBar
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTabScreen(
    onOpenSearch: () -> Unit,
    onSongItemClicked: (SongTitle) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenModel = viewModel(factory = HomeScreenModel.Factory),
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            AppTopBar(
                showUpButton = false,
                titleContent = {
                    HomeSearchBar {
                        onOpenSearch()
                    }
                },
            )
        },
        bottomBar = {
            // A workaround to apply correct bottom padding to the HomeUi.
            // The Actual Navigation Bar is provided at the top level scaffold
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0f),
            ) { }
        },
        modifier = modifier,
    ) { innerPadding ->
        HomeUi(
            state = state,
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = innerPadding,
            onSongItemClicked = onSongItemClicked,
        )
    }
}

@Composable
private fun HomeUi(
    state: ImmutableList<SongTitle>,
    onSongItemClicked: (SongTitle) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    HymnListingUi(
        hymnItems = state,
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        onSongItemClicked = onSongItemClicked,
    )
}
