package com.techbeloved.hymnbook.tunesplayback

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.IOException

class MediaPlayerAdapter : MediaPlayback {
    private var player: MediaPlayer? = null
    private var currentMetadata: MediaMetadataCompat? = null
    private var repeatTimes = 1
    private var playCount = 0

    private var playerReady = false

    private val playbackStatusSubject = PublishSubject.create<PlaybackStatus>()

    override fun onPlay() {
        player?.start()
        player?.setVolume(1.0f, 1.0f)

    }

    override fun onPause() {
        player?.pause()
    }

    override fun onStop() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    override fun duck() {
        player?.setVolume(0.3f, 0.3f)
    }

    override fun prepare(metadata: MediaMetadataCompat?): Maybe<Int> {
        if (player == null) {
            player = MediaPlayer()
        } else {
            player?.reset()
        }
        currentMetadata = metadata
        player?.setOnCompletionListener { mp ->
            playCount++
            if (playCount < repeatTimes) {
                // Play again
                onPlay()
            } else {
                playbackStatusSubject.onNext(PlaybackStatus.PlaybackComplete(metadata))
                // We want to reset the play count so that when a user clicks play after previous playback has complete,
                // we could start playback while still reporting the correct playback state such as playback position
                playCount = 0
            }

        }
        player?.setOnErrorListener { mp, what, extra ->
            playbackStatusSubject.onNext(PlaybackStatus.PlaybackError(Throwable("MediaPlayer error: $what, extra: $extra")))
            release()
            true
        }
        return Maybe.create { emitter ->

            try {
                if (metadata != null) {
                    player?.setDataSource(metadata.mediaUri.toString())
                    player?.prepare()
                    playCount = 0
                    emitter.onSuccess(player?.duration!! * repeatTimes)
                    playerReady = true
                } else {
                    emitter.onSuccess(0)
                    release()
                }
            } catch (e: Exception) {
                when (e) {
                    is IllegalStateException -> emitter.onError(Throwable("MediaPlayer setDataSource or prepare called in invalid state!", e))
                    is IOException -> emitter.onError(Throwable("Failure accessing the specified file! ${metadata?.mediaUri}", e))
                    is IllegalArgumentException -> emitter.onError(Throwable("Invalid argument!  ${metadata?.mediaUri}", e))
                    else -> emitter.onError(Throwable("Error preparing media for playback", e))
                }
                release()
            }
        }
    }

    override fun playbackStatus(): Observable<PlaybackStatus> = playbackStatusSubject.hide()

    private fun release() {
        player?.release()
        player = null
        playerReady = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun setPlaybackSpeed(speed: Float) {
        val wasPlaying = player?.isPlaying ?: false
        player?.playbackParams = PlaybackParams().setSpeed(speed)
        if (!wasPlaying && isPlaying()) {
            // When playback speed is set, the playback resumes automatically even if it was paused.
            // To ensure that doesn't happen, cache the playing state before setting the speed,
            // then pause here if it was not playing originally
            onPause()
        }
    }

    /**
     * We want to calculate the total time the song has played adding the current position
     */
    override fun currentPosition(): Long {
        val position = player?.currentPosition?.toLong() ?: 0
        val duration = player?.duration?.toLong() ?: 0
        Timber.i("duration: %s, playCount: %s", duration, playCount)
        return duration * playCount + position
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun playbackRate(): Float {
        return when {
            playerReady -> player?.playbackParams?.speed ?: 1.0f
            else -> 1.0f
        }
    }

    override fun setRepeat(@PlaybackStateCompat.RepeatMode repeatMode: Int, repeatTimes: Int) {

        when (repeatMode) {
            // For repeat mode all, it's basically going to play the same song a specified number of times
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                this.repeatTimes = repeatTimes
                player?.isLooping = false
            }
            PlaybackStateCompat.REPEAT_MODE_ONE -> {
                player?.isLooping = true
            }
            else -> {
                this.repeatTimes = 1
                player?.isLooping = false
            }
        }
    }

}