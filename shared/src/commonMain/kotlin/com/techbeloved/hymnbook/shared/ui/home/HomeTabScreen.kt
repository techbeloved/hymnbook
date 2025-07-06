package com.techbeloved.hymnbook.shared.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Sort
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.listing.SongListingUi
import com.techbeloved.hymnbook.shared.ui.songbook.SongbookSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTabScreen(
    onOpenSearch: () -> Unit,
    onSongItemClicked: (SongTitle) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenModel = viewModel(factory = HomeScreenModel.Factory),
) {
    LaunchedEffect(Unit) {
        viewModel.onScreenLoaded()
    }
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            AppTopBar(
                showUpButton = false,
                scrollBehaviour = scrollBehavior,
                titleContent = {
                    if (!state.isLoading) {
                        SongbookSelector(
                            songbooks = state.songbooks,
                            selectedSongbook = state.currentSongbook,
                            onSongbookSelected = viewModel::onUpdateSongbook,
                        )
                    }
                },
                actions = {
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onOpenSearch, modifier = Modifier) {
                        Icon(imageVector = Icons.TwoTone.Search, contentDescription = "Search")
                    }
                    Spacer(Modifier.width(12.dp))
                    SortByButton(
                        onSortBy = viewModel::onUpdateSortBy,
                        sortBy = state.sortBy,
                    )
                    Spacer(Modifier.width(16.dp))
                }
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
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            SongListingUi(
                songItems = state.songTitles,
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                contentPadding = innerPadding,
                onSongItemClicked = onSongItemClicked,
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun SortByButton(
    sortBy: SortBy,
    onSortBy: (SortBy) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier,
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.TwoTone.Sort,
                contentDescription = "Sort By",
            )
        }

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            matchTextFieldWidth = false,
            shape = MaterialTheme.shapes.medium,
        ) {
            SortBy.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(text = entry.label) },
                    onClick = {
                        onSortBy(entry)
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    trailingIcon = {
                        RadioButton(selected = entry == sortBy, onClick = null)
                    }
                )
                if (entry != SortBy.entries.last()) {
                    HorizontalDivider()
                }
            }

        }
    }
}
