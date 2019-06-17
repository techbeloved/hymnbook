package com.techbeloved.hymnbook.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.data.model.Playlist
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PlaylistsViewModel(private val playlistsRepo: PlaylistsRepo,
                         private val schedulerProvider: SchedulerProvider) : ViewModel() {

    private val playlistsLive = MutableLiveData<Lce<List<HymnItemModel>>>()

    val playlistsData: LiveData<Lce<List<HymnItemModel>>>
        get() = playlistsLive

    private val _playlistStatus: MutableLiveData<PlaylistStatus> = MutableLiveData()
    val playlistStatus: LiveData<PlaylistStatus> get() = _playlistStatus

    private val disposables = CompositeDisposable()

    private val deleteSubject: PublishSubject<DeleteAction> = PublishSubject.create()

    private val refreshSubject: PublishSubject<String> = PublishSubject.create()

    init {
        loadPlaylists()
        setupDeleteLogic()
    }

    private fun loadPlaylists() {
        refreshSubject.startWith("refresh").switchMap { playlistsRepo.getPlaylists() }
                .compose(playlistsToModels())
                .compose(lceMapper())
                .startWith(Lce.Loading(true))
                .observeOn(schedulerProvider.ui())
                .subscribe({ playlistsLive.value = it },
                        {
                            playlistsLive.value = Lce.Error(it.message ?: "error getting playlists")
                        })
                .let { disposables.add(it) }
    }


    private fun <T> lceMapper(): ObservableTransformer<List<T>, Lce<List<T>>> {
        return ObservableTransformer { upstream ->
            upstream.map { Lce.Content(it) }
        }

    }

    private fun playlistsToModels(): ObservableTransformer<List<Playlist>, List<HymnItemModel>> {
        return ObservableTransformer { upstream ->
            upstream.map { playlists -> playlists.map { TitleItem(it.id, it.title, it.description, it.description) } }
        }
    }

    /**
     * When a delete action is fired, we wait for like 5 seconds before proceeding. If the user chooses to cancel the request, then we do not proceed
     */
    private fun setupDeleteLogic() {

        deleteSubject.switchMap { action ->
            when (action) {
                is DeleteAction.Delete -> Observable.timer(5, TimeUnit.SECONDS)
                        .flatMap {
                            playlistsRepo.deletePlaylistById(action.playlistId)
                                    .andThen(Observable.just(PlaylistStatus.Deleted)).cast(PlaylistStatus::class.java)
                        }
                        .onErrorResumeNext(Observable.just(PlaylistStatus.Error(Throwable("Error deleting playlist")), PlaylistStatus.None))
                is DeleteAction.Cancel -> Observable.just(PlaylistStatus.None)
            }
        }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ _playlistStatus.value = it }, { Timber.w(it) })
                .let { disposables.add(it) }
    }

    /**
     * Use to initiate playlist delete. The playlist is temporarily removed from the visible list. If the user chooses to undo,
     * we want to cancel the delete request
     */
    fun requestPlaylistDelete(playlistId: Int) {
        val currentList = when (val currentValue = playlistsLive.value) {
            is Lce.Content -> currentValue.content
            is Lce.Loading -> emptyList()
            is Lce.Error -> emptyList()
            null -> emptyList()
        }
        playlistsLive.value = Lce.Content(currentList.filter { it.id != playlistId })

        _playlistStatus.value = PlaylistStatus.DeleteRequested
        deleteSubject.onNext(DeleteAction.Delete(playlistId))
        _playlistStatus.value = PlaylistStatus.None
    }

    fun undoDelete() {
        deleteSubject.onNext(DeleteAction.Cancel)
        refreshSubject.onNext("undoRefresh")
    }


    class Factory(private val playlistsRepo: PlaylistsRepo, val schedulerProvider: SchedulerProvider) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PlaylistsViewModel(playlistsRepo, schedulerProvider) as T
        }

    }
}

sealed class PlaylistStatus {
    object Deleted : PlaylistStatus()
    object DeleteRequested : PlaylistStatus()
    object None : PlaylistStatus()
    data class Error(val error: Throwable) : PlaylistStatus()
}

sealed class DeleteAction {
    data class Delete(val playlistId: Int) : DeleteAction()
    object Cancel : DeleteAction()
}
