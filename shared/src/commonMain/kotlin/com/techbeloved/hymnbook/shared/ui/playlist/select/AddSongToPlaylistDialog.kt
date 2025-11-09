@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.playlist.select

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable

@OptIn(ExperimentalComposeUiApi::class)
@Serializable
internal data class AddSongToPlaylistDialog(
    val songId: Long,
)

@Composable
internal fun AddSongToPlaylistDialog(
    onDismiss: (successMessage: String?) -> Unit,
    onCreateNewPlaylist: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddSongToPlaylistViewModel = viewModel(factory = AddSongToPlaylistViewModel.Factory),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = { onDismiss(null) },
        modifier = modifier,
        scrimColor = Color.Transparent,
    ) {
        Text(
            text = "Add Song to Playlist",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onCreateNewPlaylist() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Row {
                Text("Create a new playlist")
                Spacer(Modifier.width(8.dp))
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
        ) {

            items(state.playlists, key = { it.id }) { playlist ->
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(playlist.name)
                        Spacer(Modifier.weight(1f))
                        FilledTonalIconButton(
                            onClick = {
                                viewModel.onSelectPlaylist(playlist.id)
                                onDismiss("Song added to playlist - ${playlist.name}")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add song to ${playlist.name}"
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
