package com.techbeloved.media

import com.splendo.kaluga.media.DefaultMediaManager
import com.splendo.kaluga.media.DefaultMediaPlayer
import com.splendo.kaluga.media.MediaPlayer
import com.splendo.kaluga.media.PlaybackStateRepo
import com.splendo.kaluga.media.duration
import com.splendo.kaluga.media.mediaSourceFromUrl
import com.splendo.kaluga.media.playTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.time.Duration.Companion.milliseconds
import com.splendo.kaluga.media.PlaybackState as KalugaState

class KalugaPlaybackController(
    private val state: PlaybackState,
    coroutineScope: CoroutineScope,
) : PlaybackController {
    private val queue = mutableListOf<AudioItem>()

    private val controllerScope = coroutineScope + SupervisorJob()
    private val controllerContext = Dispatchers.Main + SupervisorJob()
    private val playbackStateRepo = PlaybackStateRepo(
        DefaultMediaManager(
            mediaSurfaceProvider = null,
            settings = DefaultMediaManager.Settings(
                playInBackground = false,
                playAfterDeviceUnavailable = false,
            ),
            coroutineContext = controllerContext,
        ), controllerContext
    )
    private val mediaPlayer = DefaultMediaPlayer({
        playbackStateRepo
    }, controllerContext)

    private val controls =
        mediaPlayer.controls.stateIn(
            controllerScope,
            SharingStarted.Eagerly,
            MediaPlayer.Controls()
        )

    init {
        controllerScope.launch {
            combine(
                mediaPlayer.duration,
                mediaPlayer.playTime(100.milliseconds)
            ) { duration, playtime ->
                duration to playtime
            }.collect { (duration, playtime) ->
                state.duration = duration.inWholeMilliseconds
                state.position = playtime.inWholeMilliseconds
            }
        }
        controllerScope.launch {
            playbackStateRepo.collect {
                state.isPlaying = it is KalugaState.Playing
                println("KalugaState: $it")
                when (it) {
                    is KalugaState.Error -> state.playerState = PlayerState.Ended
                    is KalugaState.Initialized -> state.playerState = PlayerState.Ready
                    is KalugaState.Idle -> state.playerState = PlayerState.Idle
                    is KalugaState.Completed -> state.playerState = PlayerState.Ended
                    is KalugaState.Playing -> state.playerState = PlayerState.Ready
                    is KalugaState.Paused -> state.playerState = PlayerState.Ready
                    is KalugaState.Stopped -> state.playerState = PlayerState.Ended
                    is KalugaState.Uninitialized -> state.playerState = PlayerState.Idle
                    is KalugaState.Closed -> state.playerState = PlayerState.Ended
                }
            }
        }
    }

    override fun play() {
        controllerScope.launch {
            controls.value.play?.perform()
        }
    }

    override fun pause() {
        controllerScope.launch {
            controls.value.pause?.perform?.invoke()
        }
    }

    override fun seekTo(position: Long) {
        controllerScope.launch {
            controls.value.seek?.perform?.invoke(position.milliseconds)
        }
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

        val currentPlayerItem = mediaSourceFromUrl(queue[currentItemIndex].uri) ?: return

        controllerScope.launch {
            mediaPlayer.reset()
            mediaPlayer.initializeFor(currentPlayerItem)

            controls.value.play?.perform()
        }
    }
}
