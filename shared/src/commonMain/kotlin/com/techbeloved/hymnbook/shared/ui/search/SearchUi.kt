package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.ui.listing.SongListingUi
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchUi(
    state: SearchState,
    query: String,
    onSearch: (query: String) -> Unit,
    onSongItemClicked: (SongTitle) -> Unit,
    onQueryChange: (newQuery: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppSearchBar(
                onSearch = onSearch,
                placeholderText = "Search songs",
                onQueryChange = onQueryChange,
                query = query,
            )
        },
    ) { innerPadding ->
        when (state) {
            SearchState.Default -> {
                // Nothing to show
                // can show recent searches in the future
            }

            is SearchState.NoResult -> NoSearchResultsUi(
                state = state,
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding,
            )

            SearchState.SearchLoading -> SearchLoading(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding,
            )

            is SearchState.SearchResult -> {
                SearchResults(
                    results = state.titles,
                    contentPadding = innerPadding,
                    modifier = Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(innerPadding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    onSongItemClicked = onSongItemClicked,
                )
            }
        }
    }
}

@Composable
private fun SearchResults(
    results: ImmutableList<SongTitle>,
    onSongItemClicked: (SongTitle) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SongListingUi(
        songItems = results,
        contentPadding = contentPadding,
        modifier = modifier,
        onSongItemClicked = onSongItemClicked,
    )
}

@Composable
private fun SearchLoading(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(contentPadding))
    }
}

@Composable
private fun NoSearchResultsUi(
    contentPadding: PaddingValues,
    state: SearchState.NoResult,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No results found for \"${state.query}\"!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(contentPadding),
        )
    }
}
