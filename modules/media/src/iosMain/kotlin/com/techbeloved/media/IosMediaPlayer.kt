package com.techbeloved.media

interface IosMediaPlayer {

    fun play()

    fun pause()

    fun seekTo(position: Long)

    fun prepare()

    fun onDispose()

    fun changePlaybackSpeed(speed: Float)

    fun toggleLooping()
}
