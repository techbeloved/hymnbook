package com.techbeloved.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

@Composable
actual fun rememberPlaybackController(playbackState: PlaybackState): PlaybackController? {
    val coroutineScope = rememberCoroutineScope()
    var playbackController: PlaybackController? by remember { mutableStateOf(null) }
    DisposableEffect(playbackState) {
        val iosPlaybackController = IosPlaybackController(playbackState, coroutineScope)
        playbackController = iosPlaybackController
        onDispose {
            iosPlaybackController.onDispose()
        }
    }
    return playbackController
}
