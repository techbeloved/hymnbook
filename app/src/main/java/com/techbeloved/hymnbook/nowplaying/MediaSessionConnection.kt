package com.techbeloved.hymnbook.nowplaying

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.techbeloved.hymnbook.data.PlayerPreferences
import com.techbeloved.hymnbook.tunesplayback.EVENT_MEDIA_FILE_NOT_FOUND
import com.techbeloved.hymnbook.tunesplayback.EVENT_PLAYABLE_MEDIA_NOT_AVAILABLE
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MediaSessionConnection(context: Context, serviceComponent: ComponentName, private val playerPrefs: PlayerPreferences) {

    private val _playbackRate = MutableLiveData<Float>(1.0f)
    val playbackRate: LiveData<Float>
        get() = _playbackRate

    private val _repeatMode = MutableLiveData<Int>(PlaybackStateCompat.REPEAT_MODE_NONE)
    val repeatMode: LiveData<Int>
        get() = _repeatMode

    private val _isConnected = MutableLiveData<Boolean>()
            .apply { postValue(false) }
    val isConnected: LiveData<Boolean>
        get() = _isConnected

    private val _playbackEvent = MutableLiveData<PlaybackEvent>(PlaybackEvent.None)
    val playbackEvent: LiveData<PlaybackEvent>
        get() = _playbackEvent

    val rootMedia: String get() = mediaBrowser.root

    private val _playbackState = MutableLiveData<PlaybackStateCompat>()
            .apply { postValue(EMPTY_PLAYBACK_STATE) }
    val playbackState: LiveData<PlaybackStateCompat>
        get() = _playbackState

    private val _nowPlaying = MutableLiveData<MediaMetadataCompat>()
            .apply { postValue(NOTHING_PLAYING) }
    val nowPlaying: LiveData<MediaMetadataCompat>
        get() = _nowPlaying

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser: MediaBrowserCompat = MediaBrowserCompat(
            context,
            serviceComponent,
            mediaBrowserConnectionCallback, null
    ).apply { connect() }
    private lateinit var mediaController: MediaControllerCompat
    // TODO: expose this if ever needed
    val controller: MediaControllerCompat
        get() = mediaController

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun updatePlaybackRate(rate: Float) {
        playerPrefs.savePlaybackRate(rate)
    }


    private var disposables: CompositeDisposable? = null

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
            MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            _isConnected.postValue(true)

            disposables = CompositeDisposable()
            playerPrefs.playbackRate()
                    .subscribeOn(Schedulers.io())
                    .subscribe({ rate -> _playbackRate.postValue(rate) }, { Timber.w(it) })
                    .let { disposables?.add(it) }
            playerPrefs.repeatMode()
                    .subscribeOn(Schedulers.io())
                    .subscribe({ mode -> _repeatMode.postValue(mode) }, { Timber.w(it) })
                    .let { disposables?.add(it) }
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(false)
            disposables?.dispose()
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(false)
            disposables?.dispose()
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Timber.i("State updated: %s", state)
            _playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Timber.i("Metadata updated: %s", metadata)
            _nowPlaying.postValue(metadata ?: NOTHING_PLAYING)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            // TODO
        }

        // TODO: Implement other callbacks

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            Timber.i("Event received: %s", event)
            when (event) {
                EVENT_MEDIA_FILE_NOT_FOUND,
                EVENT_PLAYABLE_MEDIA_NOT_AVAILABLE -> {
                    _playbackEvent.postValue(PlaybackEvent.Error("Playable media not found!"))
                    GlobalScope.launch {
                        delay(200)
                        _playbackEvent.postValue(PlaybackEvent.None)
                    }

                }
                else -> _playbackEvent.postValue(PlaybackEvent.Message("Received event: $event"))
            }
        }
    }

    companion object {
        @Volatile
        private var instance: MediaSessionConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName, playerPrefs: PlayerPreferences) =
                instance ?: synchronized(this) {
                    instance
                            ?: MediaSessionConnection(context, serviceComponent, playerPrefs)
                            .also { instance = it }
                }
    }
}

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
        .build()