@file:kotlin.OptIn(ExperimentalResourceApi::class)

package com.techbeloved.media

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import hymnbook.modules.media.generated.resources.Res
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.ExperimentalResourceApi

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                PlayerControlViewPreview()
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun MediaPlayerControls(modifier: Modifier = Modifier) {

    val playbackState = rememberPlaybackState()
    val mediaController by rememberPlaybackController(playbackState)
    LaunchedEffect(playbackState) {
        snapshotFlow {
            Triple(
                playbackState.position,
                playbackState.duration,
                playbackState.isPlaying
            )
        }
            .collect {
                println("Now playing: duration ${it.second}, position ${it.first}, isPlaying ${it.third}")
            }
    }
    if (mediaController != null) {
        val controller = mediaController!!
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { controller.seekToPrevious() }) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "Skip to previous",
                )
            }

            IconButton(onClick = {
                controller.run {
                    when (playbackState.playerState) {
                        PlayerState.Idle -> {
                            loadMediaItems(controller)
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

            IconButton(onClick = { controller.seekToNext() }) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Skip to next",
                )
            }
        }
    }
}

private fun loadMediaItems(mediaController: PlaybackController?) {
    mediaController?.setItems(
        persistentListOf(
            AudioItem(
                uri = Res.getUri("files/sample1.mp3"),
                title = "",
                artist = "",
                album = ""
            ),
            AudioItem(
                uri = Res.getUri("files/sample2.mp3"),
                title = "",
                artist = "",
                album = ""
            ),
            AudioItem(
                uri = Res.getUri("files/sample3.mp3"),
                title = "",
                artist = "",
                album = ""
            ),
            AudioItem(
                uri = Res.getUri("files/sample4.mp3"),
                title = "",
                artist = "",
                album = ""
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
