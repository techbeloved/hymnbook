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

class AndroidPlaybackController(
    private val mediaController: MediaController,
    scope: CoroutineScope,
    private val state: PlaybackState,
) : PlaybackController {

    init {
        // Initial state
        state.apply {
            playerState = playerState()
            isPlaying = mediaController.isPlaying
            itemIndex = mediaController.currentMediaItemIndex
            duration = mediaController.contentDuration
            position = mediaController.currentPosition
        }

        scope.launch {
            while (isActive) {
                state.position = mediaController.currentPosition
                delay(timeMillis = 100)
            }
        }

        scope.launch {
            mediaController.listen { events ->
                if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                    state.isPlaying = mediaController.isPlaying
                    state.duration = mediaController.contentDuration
                }

                if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                    state.itemIndex = mediaController.currentMediaItemIndex
                    state.duration = mediaController.contentDuration
                }
                if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
                    state.itemIndex = mediaController.currentMediaItemIndex
                    state.duration = mediaController.contentDuration

                }
                if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)) {
                    state.position = mediaController.currentPosition
                    state.duration = mediaController.contentDuration
                }

                if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                    state.playerState = playerState()
                }
            }
        }
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
                    .setUri(item.uri)
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
}
