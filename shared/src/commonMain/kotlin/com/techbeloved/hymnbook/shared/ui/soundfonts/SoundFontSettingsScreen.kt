@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.soundfonts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.media.download.MediaDownloadState
import kotlinx.serialization.Serializable

@Serializable
internal object SoundFontSettingsScreen

@Composable
internal fun SoundFontSettingsScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundFontSettingsViewModel = viewModel(factory = SoundFontSettingsViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        modifier = modifier,
        scrimColor = Color.Transparent,
    ) {
        Text(
            text = "Choose a sound font",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Text(
            text = "To play midi tunes, you  need a sound font",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.heightIn(min = 200.dp)) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                    )
                }
            } else if (state.items.isEmpty()) {
                Text(
                    text = "No sound fonts found",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(state.items) { item ->
                        ListItem(
                            modifier = Modifier
                                .height(56.dp)
                                .clickable {
                                    if (item.isDownloaded) {
                                        viewModel.onItemClicked(item)
                                    } else {
                                        viewModel.onDownloadItemClicked(item)
                                    }
                                },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = { Text(text = item.displayName) },
                            trailingContent = {
                                when {
                                    item.isDownloaded -> {
                                        if (item.isPreferred) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Is selected"
                                            )
                                        }
                                    }

                                    item.downloadState != null -> {
                                        when (item.downloadState) {
                                            is MediaDownloadState.Downloading -> {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(32.dp),
                                                    progress = {
                                                        item.downloadState.progress
                                                    })
                                            }

                                            is MediaDownloadState.Error -> {
                                                Text(text = "Error down…")
                                            }

                                            MediaDownloadState.Initializing -> {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(
                                                        32.dp
                                                    )
                                                )
                                            }

                                            MediaDownloadState.Success -> {
                                                // Success animation
                                            }
                                        }
                                    }

                                    else -> {
                                        // Not downloaded
                                        DownloadButton(
                                            item = item,
                                            onClick = { viewModel.onDownloadItemClicked(item) },
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadButton(
    item: SoundFontItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Download",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = item.fileSize,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                    ),
                )
            }

            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null
            )
        }
    }
}
