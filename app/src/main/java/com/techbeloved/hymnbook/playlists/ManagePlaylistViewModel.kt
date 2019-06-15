package com.techbeloved.hymnbook.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.Playlist
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * A [ViewModel] used to manage playlists. It handles things such as
 *  playlist creation, deletion, adding of songs to playlist, etc
 */
class ManagePlaylistViewModel(private val playlistsRepo: PlaylistsRepo, private val schedulerProvider: SchedulerProvider) : ViewModel() {

    private var disposables: CompositeDisposable = CompositeDisposable()

    init {
        loadPlaylists()
    }

    private val _favoriteSaved: MutableLiveData<FavoriteState> = MutableLiveData()
    val favoriteSaved: LiveData<FavoriteState>
        get() = _favoriteSaved

    private var selectedHymnId: Int by Delegates.notNull()

    private val _playlists = MutableLiveData<List<TitleItem>>()
    val playlists: LiveData<List<TitleItem>>
        get() = _playlists

    private fun loadPlaylists() {
        playlistsRepo.getPlaylists()
                .doOnNext { Timber.i("Received: %s", it) }
                .map { it.map { playlist -> TitleItem(playlist.id, playlist.title, "", playlist.description) } }
                .observeOn(schedulerProvider.ui())
                .subscribe({ _playlists.value = it }, { Timber.w(it) })
                .let { disposables.add(it) }
    }

    /**
     * Update the hymn id that will be later saved to playlist
     */
    fun updateSelectedHymnId(hymnId: Int) {
        selectedHymnId = hymnId
    }

    fun addSelectedHymnToPlaylist(playlistId: Int) {
        playlistsRepo.saveFavorite(Favorite(playlistId = playlistId, hymnId = selectedHymnId))
                .andThen(Observable.timer(2, TimeUnit.SECONDS)
                        .map<FavoriteState> { FavoriteState.Dismiss }
                        .startWith(FavoriteState.Saved)
                )
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ _favoriteSaved.value = it }, { error ->
                    _favoriteSaved.value = FavoriteState.SaveFailed(error)
                    Timber.i(error)
                })
                .let { disposables.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    /**
     * Receives the playlist information, creates a new playlist in database, and then adds the currently selected hymn as favorite
     * A delay is added so that the message can be shown to the user
     */
    fun saveFavoriteInNewPlaylist(playlistCreate: PlaylistEvent.Create) {
        playlistsRepo.savePlaylist(Playlist(
                title = playlistCreate.title,
                description = playlistCreate.description,
                created = playlistCreate.created))
                .flatMapCompletable { playlistId -> playlistsRepo.saveFavorite(Favorite(playlistId = playlistId, hymnId = selectedHymnId)) }
                .andThen(Observable.timer(2, TimeUnit.SECONDS)
                        .map<FavoriteState> { FavoriteState.Dismiss }
                        .startWith(FavoriteState.Saved)
                )
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ _favoriteSaved.value = it }, { error ->
                    _favoriteSaved.value = FavoriteState.SaveFailed(error)
                    Timber.w(error)
                })
                .let { disposables.add(it) }
    }

    class Factory(private val playlistsRepo: PlaylistsRepo, private val schedulerProvider: SchedulerProvider) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ManagePlaylistViewModel(playlistsRepo, schedulerProvider) as T
        }
    }

    sealed class FavoriteState {
        object Saved : FavoriteState()
        data class SaveFailed(val error: Throwable) : FavoriteState()
        object Dismiss : FavoriteState()
    }
}