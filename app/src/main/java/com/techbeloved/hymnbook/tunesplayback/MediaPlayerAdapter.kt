package com.techbeloved.hymnbook.tunesplayback

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.RequiresApi
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.IOException

class MediaPlayerAdapter : MediaPlayback {
    private var player: MediaPlayer? = null

    private val playbackStatusSubject = PublishSubject.create<PlaybackStatus>()

    override fun onPlay() {
        player?.setVolume(1.0f, 1.0f)
        player?.start()
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
        player?.setOnCompletionListener { mp -> playbackStatusSubject.onNext(PlaybackStatus.PlaybackComplete(metadata)) }
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
                    emitter.onSuccess(player?.duration!!)
                } else {
                    emitter.onSuccess(0)
                }
            } catch (e: Exception) {
                when (e) {
                    is IllegalStateException -> emitter.onError(Throwable("MediaPlayer setDataSource or prepare called in invalid state!", e))
                    is IOException -> emitter.onError(Throwable("Failure accessing the specified file! ${metadata?.mediaUri}", e))
                    is IllegalArgumentException -> emitter.onError(Throwable("Invalid argument!  ${metadata?.mediaUri}", e))
                    else -> emitter.onError(Throwable("Error preparing media for playback", e))
                }
            }
        }
    }

    override fun playbackStatus(): Observable<PlaybackStatus> = playbackStatusSubject.hide()

    private fun release() {
        player?.release()
        player = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun setPlaybackSpeed(speed: Float) {
        player?.playbackParams = PlaybackParams().setSpeed(speed)
    }

    override fun currentPosition(): Long = player?.currentPosition?.toLong() ?: -1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun playbackRate(): Float = player?.playbackParams?.speed ?: 1.0f

}