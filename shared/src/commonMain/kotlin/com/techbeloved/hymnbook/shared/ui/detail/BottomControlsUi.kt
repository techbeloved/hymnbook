package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.techbeloved.media.AudioItem
import com.techbeloved.media.PlaybackController
import com.techbeloved.media.PlayerState
import com.techbeloved.media.rememberPlaybackController
import com.techbeloved.media.rememberPlaybackState
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun BottomControlsUi(
    title: String,
    audioItem: AudioItem?,
    onPreviousButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousButtonClick) {
                Icon(imageVector = Icons.Rounded.ChevronLeft, contentDescription = "Previous")
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )
            IconButton(onClick = onNextButtonClick) {
                Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = "Next")
            }
        }

        if (audioItem != null) {
            PlayButton(audioItem)
        }
    }
}

@Composable
private fun PlayButton(
    audioItem: AudioItem,
    modifier: Modifier = Modifier,
) {
    val playbackState = rememberPlaybackState()
    val controller = rememberPlaybackController(playbackState)

    var progressVisible by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(playbackState) {
        snapshotFlow {
            object {
                val progress = playbackState.position.toFloat() / playbackState.duration
                val isPlaying = playbackState.isPlaying
            }
        }.collect {
            progress = it.progress
            progressVisible = it.isPlaying
        }
    }

    LaunchedEffect(audioItem, playbackState.mediaId) {
        // Decide if we need to play the new track automatically.
        // For example, the user swipes to the next page while the current song is still playing.
        if (audioItem.mediaId != playbackState.mediaId) {
            controller?.run {
                if (playbackState.isPlaying) {
                    playNew(audioItem)
                } else if (playbackState.playerState != PlayerState.Idle) {
                    prepareNew(audioItem)
                }
            }
        }
    }
    AnimatedVisibility(
        visible = controller != null,
        modifier = modifier.size(44.dp),
    ) {
        IconButton(
            onClick = {
                controller?.run {
                    when (playbackState.playerState) {
                        PlayerState.Idle -> {
                            playNew(audioItem)
                        }

                        PlayerState.Ended -> {
                            seekTo(position = 0)
                            play()
                        }

                        PlayerState.Ready -> {
                            if (playbackState.isPlaying) pause() else play()
                        }

                        PlayerState.Buffering -> {
                            // Buffering
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        ) {
            Icon(
                imageVector = if (playbackState.isPlaying) {
                    Icons.Rounded.Pause
                } else {
                    Icons.Rounded.PlayArrow
                },
                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
            )
        }
        AnimatedVisibility(progressVisible) {
            CircularProgressIndicator(strokeWidth = 2.dp, progress = { progress })
        }
    }
}

private fun PlaybackController.playNew(audioItem: AudioItem) {
    pause()
    prepareNew(audioItem)
    playWhenReady()
}

private fun PlaybackController.prepareNew(audioItem: AudioItem) {
    setItems(persistentListOf(audioItem))
    prepare()
    seekTo(position = 0)
}
