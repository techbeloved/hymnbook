@file:OptIn(ExperimentalForeignApi::class)

package com.techbeloved.media

import com.splendo.kaluga.base.kvo.observeKeyValueAsFlow
import kotlinx.cinterop.ExperimentalForeignApi
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
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake

private const val THOUSAND = 1000

class DefaultMediaPlayer(
    private val playerItem: AVPlayerItem,
    private val coroutineScope: CoroutineScope,
    private val state: PlaybackState,
) : IosMediaPlayer {
    private val player = AVPlayer.playerWithPlayerItem(playerItem)
    private var timeObserver: Any? = null
    private var playerJob: Job? = null

    override fun play() {
        if (playerItem.duration == player.currentTime()) {
            // Reset position
            seekTo(0)
        }
        player.play()
    }

    override fun pause() = player.pause()

    override fun seekTo(position: Long) =
        player.seekToTime(CMTimeMake(position, timescale = THOUSAND))

    override fun prepare() {
        changePlaybackSpeed(state.playbackSpeed.ratePercentToFloat)
        observePlaybackStatus(playerItem)
    }

    override fun changePlaybackSpeed(speed: Float) {
        player.rate = speed
    }

    override fun onDispose() {
        player.pause()
        timeObserver?.let { player.removeTimeObserver(it) }
        playerJob?.cancel()
    }

    private fun observePlaybackStatus(currentPlayerItem: AVPlayerItem) {
        timeObserver = player.addPeriodicTimeObserverForInterval(
            interval = CMTimeMake(value = 100, timescale = 1000),
            queue = null
        ) {
            state.position = (CMTimeGetSeconds(it) * THOUSAND).toLong()
        }

        playerJob = coroutineScope.launch {
            combine(
                player.observeKeyValueAsFlow<AVPlayerTimeControlStatus>("timeControlStatus"),
                currentPlayerItem.observeKeyValueAsFlow<AVPlayerItemStatus>("status"),
            ) { playStatus, status -> playStatus to status }
                .collect { (timeControlStatus, itemStatus) ->
                    when (itemStatus) {
                        AVPlayerItemStatusReadyToPlay -> {
                            state.duration =
                                (CMTimeGetSeconds(currentPlayerItem.duration) * THOUSAND).toLong()
                            state.playerState = PlayerState.Ready
                            state.playbackSpeed = player.rate.rateToPercent
                        }

                        AVPlayerItemStatusUnknown,
                        AVPlayerItemStatusFailed -> {
                            state.playerState = PlayerState.Idle
                        }
                    }

                    when (timeControlStatus) {
                        AVPlayerTimeControlStatusPlaying -> {
                            state.isPlaying = true
                            state.playbackSpeed = player.rate.rateToPercent
                        }
                        AVPlayerTimeControlStatusPaused -> state.isPlaying = false
                        AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate -> {
                            state.playerState = PlayerState.Buffering
                            state.isPlaying = true
                            state.playbackSpeed = player.rate.rateToPercent
                        }
                    }
                }
        }
    }
}
