package com.techbeloved.hymnbook.tunesplayback

import android.support.v4.media.MediaMetadataCompat
import io.reactivex.Maybe
import io.reactivex.Observable

interface MediaPlayback {
    /**
     * Starts an already prepared media player
     */
    fun onPlay()

    /**
     * Pauses the media player if it's playing
     */
    fun onPause()

    /**
     * Stops and releases the media player
     */
    fun onStop()

    /**
     * Checks that media player is playing
     */
    fun isPlaying(): Boolean

    /**
     * Reduces the volume of the media player
     */
    fun duck()

    /**
     * Called upon to prepare the media player for playback
     * This must be called before onPlay, for initial playback setup
     */
    fun prepare(metadata: MediaMetadataCompat?): Maybe<Boolean>

    fun playbackStatus(): Observable<PlaybackStatus>

}