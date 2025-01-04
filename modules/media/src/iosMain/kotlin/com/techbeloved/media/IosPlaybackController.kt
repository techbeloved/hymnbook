@file:OptIn(ExperimentalForeignApi::class)

package com.techbeloved.media

import com.splendo.kaluga.base.kvo.observeKeyValueAsFlow
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemStatus
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.AVPlayerItemStatusUnknown
import platform.AVFoundation.AVPlayerTimeControlStatus
import platform.AVFoundation.AVPlayerTimeControlStatusPaused
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSURL

class IosPlaybackController(
    private val state: PlaybackState,
    private val coroutineScope: CoroutineScope,
) : PlaybackController {
    private val queue = mutableListOf<AudioItem>()
    private var player: AVPlayer? = null
    private var timeObserver: Any? = null
    private var playerJob: Job? = null

    override fun play() {
        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun seekTo(position: Long) {
        player?.seekToTime(CMTimeMake(position, 1000))
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

    }

    override fun playWhenReady() {
        playCurrentItem()
    }

    private fun playCurrentItem() {
        val currentItemIndex = state.itemIndex
        if (!queue.indices.contains(currentItemIndex)) return

        val currentPlayerItem = queue[currentItemIndex].let {
            NSURL.URLWithString(it.uri)
                ?.let { it1 -> AVPlayerItem.playerItemWithURL(it1) }
        } ?: return

        player?.pause()
        timeObserver?.let { player?.removeTimeObserver(it) }
        playerJob?.cancel()
        player = AVPlayer.playerWithPlayerItem(currentPlayerItem)
        observePlaybackStatus(currentPlayerItem)
        player?.play()
    }

    private fun observePlaybackStatus(currentPlayerItem: AVPlayerItem) {
        timeObserver = player?.addPeriodicTimeObserverForInterval(
            interval = CMTimeMake(100, 1000),
            queue = null
        ) {
            state.position = (CMTimeGetSeconds(it) * 1000).toLong()
        }

        playerJob = coroutineScope.launch {
            combine(
                player!!.observeKeyValueAsFlow<AVPlayerTimeControlStatus>("timeControlStatus"),
                currentPlayerItem.observeKeyValueAsFlow<AVPlayerItemStatus>("status")
            ) { playStatus, status -> playStatus to status }
                .collect { (timeControlStatus, itemStatus) ->
                    when (itemStatus) {
                        AVPlayerItemStatusReadyToPlay -> {
                            state.duration =
                                (CMTimeGetSeconds(currentPlayerItem.duration) * 1000).toLong()
                            state.playerState = PlayerState.Ready
                        }

                        AVPlayerItemStatusUnknown,
                        AVPlayerItemStatusFailed -> {
                            state.playerState = PlayerState.Idle
                        }
                    }

                    when (timeControlStatus) {
                        AVPlayerTimeControlStatusPlaying -> state.isPlaying = true
                        AVPlayerTimeControlStatusPaused -> state.isPlaying = false
                        AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate -> {
                            state.playerState = PlayerState.Buffering
                            state.isPlaying = true
                        }
                    }
                }
        }
    }
}
