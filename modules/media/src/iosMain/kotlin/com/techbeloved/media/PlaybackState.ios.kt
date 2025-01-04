package com.techbeloved.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
actual fun rememberPlaybackController(playbackState: PlaybackState): PlaybackController? {
    val coroutineScope = rememberCoroutineScope()
    return remember { IosPlaybackController(playbackState, coroutineScope) }
}
