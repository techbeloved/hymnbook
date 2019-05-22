package com.techbeloved.hymnbook.hymndetail

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.IntDef
import androidx.lifecycle.*
import com.techbeloved.hymnbook.EMPTY_PLAYBACK_STATE
import com.techbeloved.hymnbook.MediaSessionConnection
import com.techbeloved.hymnbook.NOTHING_PLAYING
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.tunesplayback.id
import com.techbeloved.hymnbook.tunesplayback.isPlayEnabled
import com.techbeloved.hymnbook.tunesplayback.isPlaying
import com.techbeloved.hymnbook.tunesplayback.isPrepared
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HymnPagerViewModel(private val repository: HymnsRepository, mediaSessionConnection: MediaSessionConnection) : ViewModel() {

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
    val isConnected: LiveData<Boolean>
        get() = mediaSessionConnection.isConnected

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
        Timber.i("Updating state or metadata")
    }

    private val mediaSessionConnection = mediaSessionConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
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
