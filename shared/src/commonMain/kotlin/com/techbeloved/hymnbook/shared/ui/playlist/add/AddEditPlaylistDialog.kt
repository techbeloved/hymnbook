@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.playlist.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable

@Serializable
internal data class AddEditPlaylistDialog(
    val playlistId: Long?,
    val songId: Long?,
)

@Composable
internal fun AddEditPlaylistDialog(
    onDismiss: (saved: PlaylistSaved?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditPlaylistViewModel = viewModel(factory = AddEditPlaylistViewModel.Factory),
) {

    LaunchedEffect(Unit) {
        viewModel.onScreenLoaded()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.playlistSaved) {
        if (state.playlistSaved != null) {
            onDismiss(state.playlistSaved)
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss(null) },
        modifier = modifier,
        scrimColor = Color.Transparent,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "New Playlist",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                value = viewModel.title,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Title") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                value = viewModel.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = { Text("Description") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),

                ) {
                OutlinedButton(onClick = { onDismiss(null) }) {
                    Text("Cancel")
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = {
                        viewModel.onSavePlaylist()
                    },
                    enabled = state.isModified && !state.isLoading,
                ) {
                    Text("Create")
                }
            }
        }
    }
}
