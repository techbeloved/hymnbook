package com.techbeloved.media

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import platform.AVFoundation.AVPlayerItem

class IosPlaybackController(
    private val state: PlaybackState,
    private val coroutineScope: CoroutineScope,
) : PlaybackController {
    private val queue = mutableListOf<AudioItem>()
    private var player: IosMediaPlayer? = null

    override fun play() {
        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    override fun seekToNext() {
        val nextItemIndex = state.itemIndex + 1
        if (queue.isEmpty() || nextItemIndex > queue.lastIndex) return
        state.itemIndex = nextItemIndex
        playCurrentItem()
    }

    override fun seekToPrevious() {
        val previousItemIndex = state.itemIndex - 1
        if (queue.isEmpty() || previousItemIndex < 0) return
        state.itemIndex = previousItemIndex
        playCurrentItem()
    }

    override fun setItems(items: ImmutableList<AudioItem>) {
        // "https://cdn.pixabay.com/download/audio/2024/02/28/audio_60f7a54400.mp3"
        queue.clear()
        queue.addAll(items)
    }

    override fun prepare() {
        val currentItemIndex = state.itemIndex
        if (!queue.indices.contains(currentItemIndex)) return

        val audioItem = queue[currentItemIndex]

        val currentPlayerItem = getNSURLFromRelativePath(audioItem.relativePath) ?: return

        player?.onDispose()

        player = if (audioItem.isMidi()) {
            MidiPlayer(
                midiContent = currentPlayerItem,
                coroutineScope = coroutineScope,
                state = state,
            )
        } else {
            DefaultMediaPlayer(
                playerItem = AVPlayerItem.playerItemWithURL(currentPlayerItem),
                coroutineScope = coroutineScope,
                state = state,
            )
        }
        player?.prepare()
        // Update the current playing media id
        state.mediaId = audioItem.mediaId
    }

    override fun playWhenReady() {
        player?.play()
    }

    private fun playCurrentItem() {
        prepare()
        player?.play()
    }

    fun onDispose() = player?.onDispose()
}
