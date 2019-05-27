package com.techbeloved.hymnbook.hymndetail

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.IntDef
import androidx.lifecycle.*
import com.techbeloved.hymnbook.EMPTY_PLAYBACK_STATE
import com.techbeloved.hymnbook.MediaSessionConnection
import com.techbeloved.hymnbook.NOTHING_PLAYING
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.tunesplayback.*
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HymnPagerViewModel(private val repository: HymnsRepository, mediaSessionConnection: MediaSessionConnection) : ViewModel() {

    /**
     * Playback controls
     */
    private val _isPlaying = MutableLiveData<Boolean>(true)
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private val _hymnIndicesLiveData: MutableLiveData<Lce<List<Int>>> = MutableLiveData()

    val hymnIndicesLiveData: MutableLiveData<Lce<List<Int>>>
        get() = _hymnIndicesLiveData

    private val indicesConsumer: Consumer<in Lce<List<Int>>>? = Consumer {
        _hymnIndicesLiveData.value = it
    }

    private val errorConsumer: Consumer<in Throwable>? = Consumer {
        _hymnIndicesLiveData.value = Lce.Error("Failed to load indices of hymns")
    }

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

    private val compositeDisposable = CompositeDisposable()
    fun loadHymnIndices(@SortBy sortBy: Int = BY_NUMBER) {
        val disposable = repository.loadHymnIndices(sortBy)
                .compose(indicesToLceMapper())
                .startWith(Lce.Loading(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(indicesConsumer, errorConsumer)

        compositeDisposable.add(disposable)
    }

    private fun indicesToLceMapper(): FlowableTransformer<List<Int>, Lce<List<Int>>> = FlowableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

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

    fun playMedia(mediaId: String) {
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        Timber.i("isPrepared: %s\nmediaId: %s\nnowplayingId: %s", isPrepared, mediaId, nowPlaying?.id)
        if (isPrepared && mediaId == nowPlaying?.id) {
            mediaSessionConnection.playbackState.value?.let { playbackState ->
                Timber.i("State playing: %s, state: %s", playbackState.isPlaying, playbackState.state)
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Timber.w("Cannot play media currently")
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaId, null)
        }

    }

    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()

        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        updatePosition = false
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

    class Factory(private val provideRepository: HymnsRepository, private val mediaSessionConnection: MediaSessionConnection) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HymnPagerViewModel(provideRepository, mediaSessionConnection) as T
        }

    }
}

const val BY_TITLE = 12
const val BY_NUMBER = 13
const val BY_FAVORITE = 14

@IntDef(BY_TITLE, BY_NUMBER, BY_FAVORITE)
@Retention(AnnotationRetention.SOURCE)
annotation class SortBy

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
