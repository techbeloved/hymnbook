package com.techbeloved.media

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.listen
import androidx.media3.session.MediaController
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class AndroidPlaybackController(
    private val mediaController: MediaController,
    scope: CoroutineScope,
    private val state: PlaybackState,
) : PlaybackController {

    override val isLoopingSupported: Boolean = true

    init {
        // Initial state
        state.apply {
            playerState = playerState()
            isPlaying = mediaController.isPlaying
            itemIndex = mediaController.currentMediaItemIndex
            position = mediaController.currentPosition
            mediaId = mediaController.currentMediaItem?.mediaId
            // Convert to percent
            playbackSpeed = mediaController.playbackParameters.speed.rateToPercent
            updateDuration()
            isLooping = mediaController.repeatMode != Player.REPEAT_MODE_OFF
        }

        scope.launch {
            while (isActive) {
                // Ensure position is never more than duration
                if (mediaController.currentPosition > state.duration) {
                    updateDuration()
                }
                state.position = mediaController.currentPosition
                delay(timeMillis = 100)
            }
        }

        scope.launch {
            mediaController.listen { events ->
                if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                    state.isPlaying = mediaController.isPlaying
                    state.mediaId = mediaController.currentMediaItem?.mediaId
                }

                if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                    state.itemIndex = mediaController.currentMediaItemIndex
                    state.mediaId = mediaController.currentMediaItem?.mediaId
                    updateDuration()
                }
                if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
                    state.itemIndex = mediaController.currentMediaItemIndex
                    state.mediaId = mediaController.currentMediaItem?.mediaId
                    updateDuration()

                }
                if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)) {
                    state.position = mediaController.currentPosition
                    updateDuration()
                }

                if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                    state.playerState = playerState()
                    state.isPlaying = mediaController.isPlaying
                }
                if (events.contains(Player.EVENT_TIMELINE_CHANGED)) {
                    updateDuration()
                }

                if (events.contains(Player.EVENT_PLAYBACK_PARAMETERS_CHANGED)) {
                    state.playbackSpeed = mediaController.playbackParameters.speed.rateToPercent
                }

                if (events.contains(Player.EVENT_REPEAT_MODE_CHANGED)) {
                    state.isLooping = mediaController.repeatMode != Player.REPEAT_MODE_OFF
                }
            }
        }
    }

    private fun updateDuration() {
        state.duration = abs(mediaController.contentDuration)
    }

    private fun playerState() = when (mediaController.playbackState) {
        Player.STATE_BUFFERING -> PlayerState.Buffering
        Player.STATE_ENDED -> PlayerState.Ended
        Player.STATE_IDLE -> PlayerState.Idle
        Player.STATE_READY -> PlayerState.Ready
        else -> PlayerState.Idle
    }

    override fun play() {
        mediaController.play()
    }

    override fun pause() {
        mediaController.pause()
    }

    override fun seekTo(position: Long) {
        mediaController.seekTo(position)
    }

    override fun seekToNext() {
        mediaController.seekToNext()
    }

    override fun seekToPrevious() {
        mediaController.seekToPrevious()
    }

    override fun setItems(items: ImmutableList<AudioItem>) {
        mediaController.setMediaItems(
            items.map { item ->
                MediaItem.Builder()
                    .setUri(item.absolutePath)
                    .setMediaId(item.mediaId)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(item.title)
                            .setArtist(item.artist)
                            .setAlbumTitle(item.album)
                            .build()
                    ).build()
            },
        )
    }

    override fun prepare() {
        mediaController.prepare()
    }

    override fun playWhenReady() {
        mediaController.playWhenReady = true
    }

    override fun changePlaybackSpeed(speed: Int) {
        mediaController.setPlaybackSpeed(speed.ratePercentToFloat)
    }

    override fun toggleLooping() {
        mediaController.repeatMode = if (mediaController.repeatMode == Player.REPEAT_MODE_OFF) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }
}
