@file:OptIn(ExperimentalResourceApi::class)

package com.techbeloved.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hymnbook.modules.media.generated.resources.Res
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MediaPlayerControls(modifier: Modifier = Modifier) {

    val playbackState = rememberPlaybackState()
    val mediaController = rememberPlaybackController(playbackState)
    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(playbackState) {
        snapshotFlow {
            playbackState.position.toFloat() / playbackState.duration
        }.collect { progress = it }
    }
    LaunchedEffect(playbackState) {
        snapshotFlow { playbackState.playerState }
            .collect { println("State: $it") }
    }
    if (mediaController != null) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = { progress })
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { mediaController.seekToPrevious() }) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Skip to previous",
                    )
                }

                IconButton(onClick = {
                    mediaController.run {
                        when (playbackState.playerState) {
                            PlayerState.Idle -> {
                                loadMediaItems(mediaController)
                                prepare()
                                playWhenReady()
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
                }) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) {
                            Icons.Rounded.PauseCircle
                        } else {
                            Icons.Default.PlayCircle
                        },
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                    )
                }

                IconButton(onClick = { mediaController.seekToNext() }) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Skip to next",
                    )
                }
            }
        }
    }
}

private fun loadMediaItems(mediaController: PlaybackController?) {
    mediaController?.setItems(
        persistentListOf(
            AudioItem(
                uri = Res.getUri("files/sample5.mid"),
                title = "Midi with joy",
                artist = "Gospel artist",
                album = "Demo"
            ),
            AudioItem(
                uri = Res.getUri("files/sample2.mp3"),
                title = "Sample beats",
                artist = "Demo demo",
                album = "Demo"
            ),
            AudioItem(
                uri = Res.getUri("files/sample3.mp3"),
                title = "Dance with me beats",
                artist = "Demo",
                album = "Demo"
            ),
            AudioItem(
                uri = Res.getUri("files/sample4.mp3"),
                title = "Viertel vor acht",
                artist = "DDD",
                album = "Triple"
            ),
        )
    )
}

@Preview
@Composable
fun PlayerControlViewPreview() {
    MaterialTheme {
        MediaPlayerControls(modifier = Modifier.height(300.dp))
    }
}
