package com.techbeloved.media

import androidx.compose.runtime.Composable

@Composable
actual fun rememberPlaybackController(
    playbackState: PlaybackState,
    midiSoundFontPath: String?
): PlaybackController? = null
