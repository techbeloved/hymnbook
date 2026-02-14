package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.ui.listing.SongListingUi
import com.techbeloved.hymnbook.shared.ui.theme.AppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchUi(
    state: SearchState,
    query: String,
    onSearch: (query: String) -> Unit,
    onSongItemClicked: (SongTitle) -> Unit,
    onQueryChange: (newQuery: String) -> Unit,
    onFilterBySongbook: (songbook: String) -> Unit,
    modifier: Modifier = Modifier,
    isSpeedDial: Boolean = false,
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
                keyboardType = if (isSpeedDial) {
                    KeyboardType.Number
                } else {
                    KeyboardType.Text
                },
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            SongbooksFilter(
                songbooks = state.songbooks,
                selectedSongbook = state.selectedSongbook,
                onFilterBySongbook = onFilterBySongbook,
            )

            when {
                state.isLoading -> SearchLoading(
                    modifier = Modifier.fillMaxSize(),
                )

                state.query.isBlank() -> DefaultSearchUi(
                    recentSongs = state.recentSongs,
                    onRecentSongClicked = onSongItemClicked,
                    recentSearches = state.recentSearches,
                    onRecentSearchClicked = {
                        onQueryChange(it)
                        onSearch(it)
                    },
                    modifier = Modifier.fillMaxSize()
                )

                state.results.isEmpty() -> NoSearchResultsUi(
                    searchQuery = state.query,
                    modifier = Modifier.fillMaxSize(),
                )

                else -> {
                    SearchResults(
                        results = state.results,
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        onSongItemClicked = onSongItemClicked,
                    )
                }
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
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier)
    }
}

@Composable
private fun NoSearchResultsUi(
    searchQuery: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No results found for \"${searchQuery}\"!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier,
        )
    }
}

@Composable
private fun SongbooksFilter(
    songbooks: ImmutableList<String>,
    onFilterBySongbook: (songbook: String) -> Unit,
    selectedSongbook: String?,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    // Store the widths of each chip to calculate scroll position
    val itemWidths = remember { mutableMapOf<Int, Int>() }

    LaunchedEffect(selectedSongbook, songbooks, itemWidths.size) {
        selectedSongbook?.let { selected ->
            val selectedIndex = songbooks.indexOf(selected)
            if (selectedIndex != -1) {
                // Calculate the target scroll position
                val scrollToOffset = (0 until selectedIndex).sumOf { itemWidths[it] ?: 0 }

                coroutineScope.launch {
                    scrollState.animateScrollTo(scrollToOffset)
                }
            }
        }
    }

    Row(
        modifier = modifier.padding(vertical = 8.dp)
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(Modifier.width(16.dp))
        songbooks.forEachIndexed { index, songbook ->
            val isSelected = selectedSongbook == songbook
            FilterChip(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    // Store the width of the chip once it's laid out
                    itemWidths[index] = coordinates.size.width
                },
                onClick = { if (!isSelected) onFilterBySongbook(songbook) },
                label = {
                    Text(
                        text = songbook,
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
                border = null,
                shape = RoundedCornerShape(100.dp),
                selected = isSelected,
            )
        }
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun DefaultSearchUi(
    recentSongs: ImmutableList<SongTitle>,
    onRecentSongClicked: (SongTitle) -> Unit,
    recentSearches: ImmutableList<String>,
    onRecentSearchClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {

        if (recentSongs.isNotEmpty()) {
            item {
                Text(
                    text = "Recent songs",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(recentSongs, key = { it.id }) { item ->
                ListItem(
                    modifier = Modifier.clickable {
                        onRecentSongClicked(item)
                    },
                    headlineContent = {
                        Text(text = item.title)
                    },
                    leadingContent = {
                        Text(
                            text = item.songbookEntry.orEmpty(),
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.End,
                        )
                    },
                    supportingContent = if (item.alternateTitle.isNullOrBlank()) {
                        null
                    } else {
                        {
                            Text(text = item.alternateTitle)
                        }
                    }
                )
            }
        }

        if (recentSearches.isNotEmpty()) {
            item {
                Text(
                    text = "Recent searches",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(recentSearches) { search ->
                ListItem(
                    modifier = Modifier.clickable { onRecentSearchClicked(search) },
                    headlineContent = { Text(search) },
                    leadingContent = { Icon(Icons.Rounded.History, contentDescription = null) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SearchUiPreview() {
    AppTheme {
        SearchUi(
            state = SearchState(),
            query = "",
            onSearch = {},
            onSongItemClicked = {},
            onQueryChange = {},
            onFilterBySongbook = {},
        )
    }
}

@Preview
@Composable
private fun SearchUiWithRecentSongsPreview() {
    AppTheme {
        SearchUi(
            state = SearchState(
                recentSongs = persistentListOf(
                    SongTitle(
                        id = 1,
                        title = "Praise to the Lord the Almighty",
                        alternateTitle = null,
                        songbookEntry = "12",
                        songbook = "songbook"
                    ),
                    SongTitle(
                        id = 2,
                        title = "Song 2",
                        alternateTitle = "Alternate Title 2",
                        songbookEntry = "13",
                        songbook = "songbook"
                    ),
                ),
                recentSearches = persistentListOf("Search 1", "Search 2"),
            ),
            query = "",
            onSearch = {},
            onSongItemClicked = {},
            onQueryChange = {},
            onFilterBySongbook = {},
        )
    }
}
