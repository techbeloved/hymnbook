package com.techbeloved.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * The returned [PlayerState] is used to observe the state of the playback. e.g, isPlaying, isBuffering,
 * payback position, etc
 */
@Composable
fun rememberPlaybackState(): PlaybackState =
    rememberSaveable(saver = PlaybackState.Saver) { PlaybackState() }

/**
 * Returns the [PlaybackController] when the media session is connected otherwise null.
 * The [PlaybackController] is used to control the media playback. E.g. play/pause, seekTo, etc.
 */
@Composable
expect fun rememberPlaybackController(
    playbackState: PlaybackState = rememberPlaybackState(),
): PlaybackController?

@Stable
class PlaybackState(
    isPlaying: Boolean = false,
    position: Long = 0L,
    duration: Long = 0L,
    itemIndex: Int = 0,
    playbackSpeed: Int = 100,
    playerState: PlayerState = PlayerState.Idle,
    mediaId: String? = null,
    isLooping: Boolean = false,
) {

    var isPlaying by mutableStateOf(isPlaying)
        internal set
    var position by mutableStateOf(position)
        internal set
    var itemIndex by mutableStateOf(itemIndex)
        internal set
    var duration by mutableLongStateOf(duration)
        internal set
    var playerState by mutableStateOf(playerState)
    var playbackSpeed by mutableStateOf(playbackSpeed)
        internal set
    var isLooping by mutableStateOf(isLooping)
        internal set

    /**
     * The currently playing media id.
     */
    var mediaId by mutableStateOf(mediaId)
        internal set

    companion object {
        val Saver: Saver<PlaybackState, *> = listSaver(
            save = {
                listOf(
                    it.isPlaying,
                    it.position,
                    it.itemIndex,
                    it.duration,
                    it.playerState,
                    it.mediaId,
                    it.playbackSpeed,
                    it.isLooping,
                )
            },
            restore = {
                PlaybackState(
                    isPlaying = it[0] as Boolean,
                    position = it[1] as Long,
                    itemIndex = it[2] as Int,
                    duration = it[3] as Long,
                    playerState = it[4] as PlayerState,
                    mediaId = it[5] as String?,
                    playbackSpeed = it[6] as Int,
                    isLooping = it[7] as Boolean,
                )
            }
        )
    }
}
