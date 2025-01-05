package com.techbeloved.media

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.guava.await

@Composable
actual fun rememberPlaybackController(playbackState: PlaybackState): PlaybackController? {
    if (LocalInspectionMode.current) {
        return remember { DummyPlaybackController(playbackState) }
    }

    val context = LocalContext.current

    var playbackController: PlaybackController? by remember { mutableStateOf(null) }

    val lifecycle = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val controllerFuture = MediaController.Builder(
                context,
                SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
            ).buildAsync()
            try {
                val controller = controllerFuture.await()
                playbackController = AndroidPlaybackController(
                    mediaController = controller,
                    scope = this,
                    state = playbackState,
                )
                awaitCancellation()
            } finally {
                playbackController = null
                println("Release mediaController")
                MediaController.releaseFuture(controllerFuture)
            }
        }
    }

    return playbackController
}
