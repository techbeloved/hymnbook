package com.techbeloved.media

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.guava.await

@Composable
actual fun rememberPlaybackController(playbackState: PlaybackState): State<PlaybackController?> {
    val context = LocalContext.current

    var lifecycleStarted by remember { mutableStateOf(false) }

    LifecycleStartEffect(Unit) {
        lifecycleStarted = true
        onStopOrDispose {
            lifecycleStarted = false
        }
    }

    return produceState(null as PlaybackController?, lifecycleStarted) {
        val started = lifecycleStarted
        if (started) {
            val controllerFuture = MediaController.Builder(
                context,
                SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
            ).buildAsync()
            val controller = controllerFuture.await()

            value = AndroidPlaybackController(
                mediaController = controller,
                scope = this,
                state = playbackState,
            )
            awaitDispose {
                MediaController.releaseFuture(controllerFuture)
            }
        } else {
            value = null
        }
    }
}