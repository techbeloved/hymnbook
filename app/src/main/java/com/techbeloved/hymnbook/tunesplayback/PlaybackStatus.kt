package com.techbeloved.hymnbook.tunesplayback

import android.support.v4.media.MediaMetadataCompat

sealed class PlaybackStatus {
    data class PlaybackComplete(val metadata: MediaMetadataCompat?) : PlaybackStatus()
    data class PlaybackError(val error: Throwable) : PlaybackStatus()
}
