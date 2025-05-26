package com.techbeloved.media

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
interface PlaybackController {

    fun play()

    fun pause()

    fun seekTo(position: Long)

    fun seekToNext()

    fun seekToPrevious()

    fun setItems(items: ImmutableList<AudioItem>)

    fun prepare()

    fun playWhenReady()

    fun changePlaybackSpeed(speed: Int)
}
