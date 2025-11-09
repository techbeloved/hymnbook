@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.playlist

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.dialog.AppDialog
import com.techbeloved.hymnbook.shared.ui.theme.AppTheme
import com.techbeloved.hymnbook.shared.ui.utils.generateRandomPastelColor
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Instant

@Composable
internal fun PlayListTabScreen(
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = viewModel(factory = PlaylistsViewModel.Factory),
    onAddPlaylistClick: () -> Unit,
    onOpenPlaylistDetail: (item: PlaylistItem) -> Unit,
) {

    LaunchedEffect(Unit) {
        viewModel.onScreenLoaded()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    PlaylistsUi(
        state = state,
        onAddPlaylistClick = onAddPlaylistClick,
        onDelete = viewModel::onDeletePlaylist,
        modifier = modifier,
        onItemClick = onOpenPlaylistDetail,
    )
}

@Composable
private fun PlaylistsUi(
    state: PlaylistsUiState,
    onAddPlaylistClick: () -> Unit,
    onDelete: (PlaylistItem) -> Unit,
    onItemClick: (PlaylistItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val listState = rememberLazyListState()
    val isFabExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    Scaffold(
        topBar = {
            AppTopBar(
                showUpButton = false,
                scrollBehaviour = scrollBehavior,
                title = "Playlists",
            )
        },
        bottomBar = {
            // A workaround to apply correct bottom padding to the HomeUi.
            // The Actual Navigation Bar is provided at the top level scaffold
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0f),
            ) { }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = "New", modifier = Modifier.padding(end = 16.dp))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "New Playlist"
                    )
                },
                onClick = onAddPlaylistClick,
                shape = MaterialTheme.shapes.extraLarge,
                expanded = isFabExpanded,
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier,
    ) { innerPadding ->
        if (state.isEmpty) {
            PlaylistsEmptyUi(
                modifier = Modifier.padding(paddingValues = innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                onAddPlaylistClick = onAddPlaylistClick,
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding,
                state = listState,
            ) {
                items(items = state.playlists, key = { it.id }) { item ->
                    PlaylistItem(
                        item = item,
                        onDeleteClick = { onDelete(item) },
                        onItemClick = { onItemClick(item) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PlaylistsUiPreview() {
    val item1 = PlaylistItem(
        id = 1L,
        name = "Sunday Morning",
        description = "Hymns for sunday morning service",
        created = Instant.DISTANT_PAST,
        updated = Instant.DISTANT_PAST,
        imageUrl = null,
        songCount = 10L,
    )
    val item2 = PlaylistItem(
        id = 2L,
        name = "Wednesday evening",
        description = "Hymns for wednesday evening service",
        created = Instant.DISTANT_PAST,
        updated = Instant.DISTANT_PAST,
        imageUrl = null,
        songCount = 5L,
    )
    val state =
        PlaylistsUiState(playlists = persistentListOf(item1, item2))
    AppTheme {
        PlaylistsUi(state = state, onAddPlaylistClick = { }, onDelete = {}, onItemClick = {})
    }
}

@Preview
@Composable
private fun PlaylistsUiPreviewDark() {
    val item1 = PlaylistItem(
        id = 1L,
        name = "Sunday Morning",
        description = "Hymns for sunday morning service",
        created = Instant.DISTANT_PAST,
        updated = Instant.DISTANT_PAST,
        imageUrl = null,
        songCount = 10L,
    )
    val item2 = PlaylistItem(
        id = 2L,
        name = "Wednesday evening",
        description = "Hymns for wednesday evening service",
        created = Instant.DISTANT_PAST,
        updated = Instant.DISTANT_PAST,
        imageUrl = null,
        songCount = 5L,
    )
    val state =
        PlaylistsUiState(playlists = persistentListOf(item1, item2))
    AppTheme(darkTheme = true) {
        PlaylistsUi(state = state, onAddPlaylistClick = { }, onDelete = {}, onItemClick = {})
    }
}

@Composable
private fun PlaylistsEmptyUi(
    onAddPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            onClick = onAddPlaylistClick,
            colors = IconButtonDefaults.filledIconButtonColors(),
            modifier = Modifier.size(60.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = "Add Playlist",
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "You have not created any playlists yet. Start by clicking the button above.",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun PlaylistsEmptyUiPreview() {
    AppTheme {
        PlaylistsUi(
            onAddPlaylistClick = {},
            onDelete = {},
            state = PlaylistsUiState(playlists = persistentListOf()),
            onItemClick = {},
        )
    }
}

@Composable
private fun PlaylistItem(
    item: PlaylistItem,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text(text = item.name) },
        supportingContent = {
            Text(
                text = "${item.songCount} Songs",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier.clickable(onClick = onItemClick),
        trailingContent = {
            PlaylistItemMoreMenu(
                onDeleteClick = { showDeleteConfirmation = true },
            )
        },
        leadingContent = {
            PlaylistLetterIcon(playlistName = item.name)
        },
    )
    if (showDeleteConfirmation) {
        AppDialog(
            title = "Delete Playlist",
            content = "Are you sure you want to delete ${item.name}?",
            positiveText = "Delete",
            negativeText = "Cancel",
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                onDeleteClick()
                showDeleteConfirmation = false
            },
        )
    }

}

@Composable
private fun PlaylistItemMoreMenu(
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier,
    ) {

        IconButton(
            onClick = { },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        ) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More options")
        }

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            matchTextFieldWidth = false,
            shape = MaterialTheme.shapes.medium,
        ) {
            DropdownMenuItem(
                text = { Text(text = "Delete") },
                onClick = {
                    onDeleteClick()
                    isExpanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                    )
                }
            )
            // Add Share item
        }

    }
}


@Preview
@Composable
private fun PlaylistItemPreview() {
    val item = PlaylistItem(
        id = 1L,
        name = "My Awesome Playlist",
        description = "A collection of my favorite hymns",
        imageUrl = null,
        created = Instant.DISTANT_PAST,
        updated = Instant.DISTANT_PAST,
        songCount = 10L,
    )
    AppTheme {
        PlaylistItem(item = item, onDeleteClick = {}, onItemClick = {})
    }
}

@Preview
@Composable
private fun PlaylistLetterIconPreview() {
    AppTheme {
        PlaylistLetterIcon(
            playlistName = "Sunday Hymns",
        )
    }
}

@Composable
private fun PlaylistLetterIcon(
    playlistName: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp, // Default size, same as ListItem leading content size
    backgroundColor: Color? = null,
    textColor: Color? = null
) {
    val firstLetter = playlistName.firstOrNull()?.uppercaseChar()?.toString() ?: ""

    // Generate a consistent random color based on the playlist name, or use provided
    val bgColor = remember(playlistName, backgroundColor) {
        backgroundColor ?: generateRandomPastelColor(seed = playlistName)
    }

    val determinedTextColor = textColor ?: Color.Black.copy(alpha = .87f)

    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier.size(iconSize), contentAlignment = Alignment.Center) {
        val canvasSize = constraints.maxWidth // Assuming square icon

        Canvas(modifier = Modifier.size(iconSize)) {
            // Draw background
            drawRoundRect(
                color = bgColor,
                cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx())
            )

            // Draw letter
            if (firstLetter.isNotEmpty()) {
                val style = TextStyle(
                    color = determinedTextColor,
                    fontSize = (canvasSize * 0.5f).toSp() // Adjust font size factor as needed
                    // fontWeight = FontWeight.Bold // Optional: if you want bold
                )
                val textLayoutResult = textMeasurer.measure(
                    text = firstLetter,
                    style = style
                )
                val textWidth = textLayoutResult.size.width
                val textHeight = textLayoutResult.size.height

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = (size.width - textWidth) / 2,
                        y = (size.height - textHeight) / 2
                    )
                )
            }
        }
    }
}

