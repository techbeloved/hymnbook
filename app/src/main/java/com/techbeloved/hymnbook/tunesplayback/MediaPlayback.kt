package com.techbeloved.hymnbook.tunesplayback

import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
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
     * Should return the duration of the media
     */
    fun prepare(metadata: MediaMetadataCompat?): Maybe<Int>

    fun playbackStatus(): Observable<PlaybackStatus>

    @RequiresApi(Build.VERSION_CODES.M)
    fun setPlaybackSpeed(speed: Float)

    /**
     * Returns the current position of the player
     */
    fun currentPosition(): Long

    /**
     * Returns the current playback rate of the media player
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun playbackRate(): Float

    fun setRepeat(@PlaybackStateCompat.RepeatMode repeatMode: Int, repeatTimes: Int = 1)
}