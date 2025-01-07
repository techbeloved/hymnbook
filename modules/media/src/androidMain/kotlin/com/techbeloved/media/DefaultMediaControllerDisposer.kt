package com.techbeloved.media

import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture

class DefaultMediaControllerDisposer: MediaControllerDisposer {
    override var controllerFuture: ListenableFuture<MediaController>? = null

    @Synchronized
    override fun onDispose() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
    }

}
