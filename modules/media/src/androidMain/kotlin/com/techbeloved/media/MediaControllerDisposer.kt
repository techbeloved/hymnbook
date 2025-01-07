package com.techbeloved.media

import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture

/**
 * It's a bit tricky handling release of the controller from a composable and coroutine scope.
 * The activity onStop might complete before the coroutine cancellation is completed. We thus end up
 * with leaking activity.
 *
 * With this interface, we can handled release of the controller from the activity directly. This
 * ensures that the controller is disconnected before onStop execution is completed.
 */
interface MediaControllerDisposer {

     var controllerFuture: ListenableFuture<MediaController>?


     fun onDispose()
}
