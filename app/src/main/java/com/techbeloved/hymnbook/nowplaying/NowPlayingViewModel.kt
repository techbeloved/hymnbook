package com.techbeloved.hymnbook.nowplaying

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.techbeloved.hymnbook.tunesplayback.*
import timber.log.Timber

/**
 * [ViewModel] for interacting with the Music Player, sending and receiving playback requests and updates
 */
class NowPlayingViewModel(mediaSessionConnection: MediaSessionConnection) : ViewModel() {

    val hymnItems: MutableList<Int> = ArrayList()
    /**
     * Playback controls
     */
    private val _isPlaying = MutableLiveData<Boolean>(true)
    val isPlaying: LiveData<Boolean>
        get() = Transformations.distinctUntilChanged(_isPlaying)
    val playbackState: LiveData<PlaybackStateCompat>
        get() = mediaSessionConnection.playbackState
    val metadata: LiveData<MediaMetadataCompat>
        get() = mediaSessionConnection.nowPlaying
    val isConnected: LiveData<Boolean>
        get() = mediaSessionConnection.isConnected
    val playbackTempo: LiveData<Int>
        get() = Transformations.map(mediaSessionConnection.playbackRate) { rate ->
            Timber.i("Receiving playback rate: %s", rate)
            val progress = ((rate - 0.5f) * 10).toInt()
            Timber.i("Calculated progress: %s", progress)
            progress
        }
    val playbackRate: LiveData<Float>
        get() = mediaSessionConnection.playbackRate

    val repeatMode: LiveData<Int>
        get() = mediaSessionConnection.repeatMode

    // Playback events
    val playbackEvent: LiveData<PlaybackEvent>
        get() = mediaSessionConnection.playbackEvent

    // Media playback stuff
    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = mediaSessionConnection.nowPlaying.value ?: NOTHING_PLAYING
        if (metadata.id != null) {
            updateState(playbackState, metadata)
        }
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val metadata = it ?: NOTHING_PLAYING
        val playbackState = mediaSessionConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        if (metadata.id != null) {
            updateState(playbackState, metadata)
        }
    }

    private fun updateState(playbackState: PlaybackStateCompat, metadata: MediaMetadataCompat) {
        Timber.i("Updating state or metadata. Is playing %s", playbackState.isPlaying)
        _isPlaying.value = playbackState.isPlaying

    }

    private val handler = Handler(Looper.getMainLooper())
    private var updatePosition = true
    val mediaPosition = MutableLiveData<Long>().apply {
        postValue(0L)
    }


    /**
     * Internal function that recursively calls itself every [POSITION_UPDATE_INTERVAL_MILLIS] ms
     * to check the current playback position and updates the corresponding LiveData object when it
     * has changed.
     */
    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = playbackState.value?.currentPlaybackPosition ?: 0
        if (mediaPosition.value != currPosition)
            mediaPosition.postValue(currPosition)
        if (updatePosition)
            checkPlaybackPosition()
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    private val mediaSessionConnection = mediaSessionConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        checkPlaybackPosition()
    }


    /**
     * Request that a hymn identified by the id be played. The request is sent to the mediasession.
     * However, we want to check if new item is to be played or we should play or pause
     */
    fun playMedia(mediaId: String, isSkipping: Boolean = false) {
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        Timber.i("isPrepared: %s\nmediaId: %s\nnowplayingId: %s", isPrepared, mediaId, nowPlaying?.id)
        if (isPrepared && !isSkipping) {
            mediaSessionConnection.playbackState.value?.let { playbackState ->
                Timber.i("State playing: %s, state: %s", playbackState.isPlaying, playbackState.state)
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> {
                        if (mediaId == nowPlaying?.id) {
                            transportControls.play()
                        } else {
                            // If the item or page have changed, then play the new item instead
                            Timber.i("Should start playback of new item!")
                            transportControls.playFromMediaId(mediaId, null)
                        }
                    }
                    else -> {
                        Timber.w("Cannot play media currently")
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaId, null)
        }

    }

    /**
     * Takes progress ranging from 0 - 10, converts it to playback speed 0.5 to 1.5
     * and save in some kind of persistence
     */
    fun saveTempo(progress: Int) {

        val playbackRate = progress / 10f + 0.5f
        mediaSessionConnection.updatePlaybackRate(playbackRate)
        Timber.i("Updating playback rate: %s", playbackRate)
    }

    fun cycleRepeatMode() {
        Timber.i("Current repeat mode: %s", mediaSessionConnection.repeatMode.value)
        when (mediaSessionConnection.repeatMode.value) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> mediaSessionConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
            PlaybackStateCompat.REPEAT_MODE_ALL -> mediaSessionConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
            PlaybackStateCompat.REPEAT_MODE_ONE -> mediaSessionConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        updatePosition = false
    }

    /**
     * Hymn indices received from db are saved here for purpose of skipping to next and previous when requested
     */
    fun updateHymnItems(hymnItems: List<Int>) {
        this.hymnItems.clear()
        this.hymnItems.addAll(hymnItems)
    }

    /**
     * Called when next button is clicked on the ui. First checks that something is playing, because it is not
     * good to start playback when skip to next but something isn't playing.
     */
    fun skipTo(position: Int) {
        if (_isPlaying.value == true && position < hymnItems.size) {
            playMedia(hymnItems[position].toString(), true)
        }
    }

    class Factory(private val mediaSessionConnection: MediaSessionConnection) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NowPlayingViewModel(mediaSessionConnection) as T
        }

    }

}

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
