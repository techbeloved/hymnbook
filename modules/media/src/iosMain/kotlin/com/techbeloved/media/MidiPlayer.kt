@file:OptIn(ExperimentalForeignApi::class, ExperimentalResourceApi::class, BetaInteropApi::class)

package com.techbeloved.media

import hymnbook.modules.media.generated.resources.Res
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.AVFAudio.AVMIDIPlayer
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURL.Companion.URLWithString

class MidiPlayer(
    private val midiContent: NSURL,
    private val coroutineScope: CoroutineScope,
    private val state: PlaybackState,
) : IosMediaPlayer {

    private var isPaused = false
    private val player = memScoped {
        val error = alloc<ObjCObjectVar<NSError?>>()
        AVMIDIPlayer(
            contentsOfURL = midiContent,
            soundBankURL = URLWithString(Res.getUri("files/yamaha_grand_lite_sf_v11.sf2")),
            error = error.ptr,
        ).also {
            println("AVMidiPlayer Init: ${error.value}")
        }
    }
    private var playerEventsJob: Job? = null


    override fun play() {
        // restore playback position. The player don't have a pause so we do the state restoration manually
        seekTo(state.position)
        player.play {
            state.isPlaying = false
            if (!isPaused) {
                // Reset position
                state.position = 0
            }
            println("Playback ended. Paused: $isPaused")
            isPaused = false
        }
        observePlaybackStatus()
    }

    override fun pause() {
        player.stop()
        playerEventsJob?.cancel()
        state.isPlaying = false
        isPaused = true
    }

    override fun seekTo(position: Long) =
        player.setCurrentPosition(position / 1000.0) // Convert from millisec

    override fun prepare() {
        player.prepareToPlay()
        state.playerState = PlayerState.Ready
        state.duration = (player.duration * 1000).toLong()
        state.position = 0
    }

    override fun onDispose() {
        player.stop()
        playerEventsJob?.cancel()
    }

    private fun observePlaybackStatus() {
        playerEventsJob?.cancel()
        playerEventsJob = coroutineScope.launch {
            state.isPlaying = player.isPlaying()
            while (player.isPlaying()) {
                state.position = (player.currentPosition * 1000).toLong()
                delay(100)
            }
            state.isPlaying = player.isPlaying()
        }
    }
}
