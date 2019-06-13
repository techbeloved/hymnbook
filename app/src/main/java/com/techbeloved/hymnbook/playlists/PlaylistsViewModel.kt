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
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable

class PlaylistsViewModel(private val playlistsRepo: PlaylistsRepo,
                         private val schedulerProvider: SchedulerProvider) : ViewModel() {

    private val playlistsLive = MutableLiveData<Lce<List<HymnItemModel>>>()

    val playlistsData: LiveData<Lce<List<HymnItemModel>>>
        get() = playlistsLive

    private val disposables = CompositeDisposable()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        playlistsRepo.getPlaylists()
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


    class Factory(private val playlistsRepo: PlaylistsRepo, val schedulerProvider: SchedulerProvider) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PlaylistsViewModel(playlistsRepo, schedulerProvider) as T
        }

    }
}
