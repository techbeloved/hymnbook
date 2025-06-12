package com.techbeloved.media

import kotlinx.collections.immutable.ImmutableList

class DummyPlaybackController(private val playbackState: PlaybackState) : PlaybackController {

    override val isLoopingSupported: Boolean
        get() = false
    private val queue = mutableListOf<AudioItem>()
    override fun play() {
        playbackState.isPlaying = true
    }

    override fun pause() {
        playbackState.isPlaying = false
    }

    override fun seekTo(position: Long) {
        playbackState.position = position
    }

    override fun seekToNext() = Unit

    override fun seekToPrevious() = Unit

    override fun setItems(items: ImmutableList<AudioItem>) {
        queue.clear()
        queue.addAll(items)
    }

    override fun prepare() {
        playbackState.playerState = PlayerState.Ready
    }

    override fun playWhenReady() {
        if (playbackState.playerState == PlayerState.Ready) play()
    }

    override fun changePlaybackSpeed(speed: Int) {
        playbackState.playbackSpeed = speed
    }

    override fun toggleLooping() {
        playbackState.isLooping = !playbackState.isLooping
    }
}
