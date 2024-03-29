package com.techbeloved.hymnbook.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.Playlist
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.utils.SchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * A [ViewModel] used to manage playlists. It handles things such as
 *  playlist creation, deletion, adding of songs to playlist, etc
 */
@HiltViewModel
class ManagePlaylistViewModel @Inject constructor(
    private val playlistsRepo: PlaylistsRepo,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {

    private var _editing = false
    val editing get() = _editing

    private var _editPlaylistId by Delegates.notNull<Int>()
    val editPlaylistId get() = _editPlaylistId

    private val _playlistSaved = MutableLiveData<SaveStatus>()
    val playlistSaved: LiveData<SaveStatus> get() = _playlistSaved

    private var disposables: CompositeDisposable = CompositeDisposable()

    init {
        loadPlaylists()
    }

    private val _favoriteSaved: MutableLiveData<SaveStatus> = MutableLiveData()
    val favoriteSaved: LiveData<SaveStatus>
        get() = _favoriteSaved

    private var selectedHymnId: Int by Delegates.notNull()

    private val _playlists = MutableLiveData<List<TitleItem>>()
    val playlists: LiveData<List<TitleItem>>
        get() = _playlists

    private fun loadPlaylists() {
        playlistsRepo.getPlaylists()
            .doOnNext { Timber.i("Received: %s", it) }
            .map {
                it.map { playlist ->
                    TitleItem(
                        playlist.id,
                        playlist.title,
                        "",
                        playlist.description
                    )
                }
            }
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
            .andThen(
                Observable.just<SaveStatus>(SaveStatus.Dismiss)
                    .startWith(SaveStatus.Saved)
            )
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ _favoriteSaved.value = it }, { error ->
                _favoriteSaved.value = SaveStatus.SaveFailed(error)
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
        playlistsRepo.savePlaylist(
            Playlist(
                title = playlistCreate.title,
                description = playlistCreate.description,
                created = playlistCreate.created
            )
        )
            .flatMapCompletable { playlistId ->
                playlistsRepo.saveFavorite(
                    Favorite(
                        playlistId = playlistId,
                        hymnId = selectedHymnId
                    )
                )
            }
            .andThen(
                Observable.just<SaveStatus>(SaveStatus.Dismiss)
                    .startWith(SaveStatus.Saved)
            )
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ _favoriteSaved.value = it }, { error ->
                _favoriteSaved.value = SaveStatus.SaveFailed(error)
                Timber.w(error)
            })
            .let { disposables.add(it) }
    }

    /**
     * Saves new playlist to database
     */
    fun saveNewPlaylist(playlistCreate: PlaylistEvent.Create) {
        playlistsRepo.savePlaylist(
            Playlist(
                title = playlistCreate.title,
                description = playlistCreate.description,
                created = playlistCreate.created
            )
        )
            .flatMapObservable {
                Observable.just<SaveStatus>(SaveStatus.Dismiss)
                    .startWith(SaveStatus.Saved)
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ _playlistSaved.value = it },
                { error ->
                    _playlistSaved.value = SaveStatus.SaveFailed(error)
                    Timber.w(error)
                })
            .let { disposables.add(it) }
    }

    fun savePlaylist(playlistUpdate: PlaylistEvent.Update) {
        playlistsRepo.savePlaylist(
            playlistUpdate.id,
            playlistUpdate.title,
            playlistUpdate.description
        )
            .andThen(
                Observable.just<SaveStatus>(SaveStatus.Dismiss)
                    .startWith(SaveStatus.Saved)
            )
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ _playlistSaved.value = it },
                { error ->
                    _playlistSaved.value = SaveStatus.SaveFailed(error)
                    Timber.w(error)
                })
            .let { disposables.add(it) }
    }

    fun setEditing(editing: Boolean) {
        _editing = editing
    }

    fun setPlaylistId(playlistId: Int) {
        _editPlaylistId = playlistId
    }

    sealed class SaveStatus {
        object Saved : SaveStatus()
        data class SaveFailed(val error: Throwable) : SaveStatus()
        object Dismiss : SaveStatus()
    }
}