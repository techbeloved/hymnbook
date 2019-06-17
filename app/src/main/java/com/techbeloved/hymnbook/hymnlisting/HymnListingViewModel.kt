package com.techbeloved.hymnbook.hymnlisting

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.hymndetail.SortBy
import com.techbeloved.hymnbook.playlists.PlaylistsRepo
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.BackpressureStrategy
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers


class HymnListingViewModel(private val hymnsRepository: HymnsRepository, private val playlistsRepo: PlaylistsRepo) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _hymnTitlesLiveData = MutableLiveData<Lce<List<TitleItem>>>()
    val hymnTitlesLiveData: LiveData<Lce<List<TitleItem>>>
        get() = _hymnTitlesLiveData

    private val sortByProcessor: BehaviorProcessor<Int> = BehaviorProcessor.create()

    /**
     * Loads hymn titles sorted by the specified term. This is usually called from the fragment
     * at start and each time the sorting criteria changes
     */
    fun loadHymnTitles(@SortBy sortBy: Int) {
        sortByProcessor.onNext(sortBy)
    }

    fun loadHymnsForTopic(topicId: Int = 0) {
        val disposable = sortByProcessor
                .distinctUntilChanged()
                .switchMap { sortBy ->
                    hymnsRepository.loadHymnTitles(sortBy, topicId)
                }
                .compose(getHymnTitleUiModels())
                .compose(getViewState())
                .startWith(Lce.Loading(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> _hymnTitlesLiveData.value = result },
                        { error ->
                            _hymnTitlesLiveData.value = Lce.Error(error.message!!)
                        })

        disposables.add(disposable)
    }

    fun loadHymnsForPlaylist(playlistId: Int) {
        val disposable = sortByProcessor
                .distinctUntilChanged()
                .switchMap { sortBy ->
                    playlistsRepo.getHymnsInPlaylist(playlistId, sortBy).toFlowable(BackpressureStrategy.LATEST)
                }
                .compose(getHymnTitleUiModels())
                .compose(getViewState())
                .startWith(Lce.Loading(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> _hymnTitlesLiveData.value = result },
                        { error ->
                            _hymnTitlesLiveData.value = Lce.Error(error.message!!)
                        })

        disposables.add(disposable)
    }

    private fun convertToTitleUiModels(): FlowableTransformer<List<Hymn>, List<TitleItem>> = FlowableTransformer { upstream ->
        upstream.map { hymns -> hymns.map { TitleItem(it.num, it.title) } }
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    // Our use cases in form Transformers

    @VisibleForTesting
    fun getHymnTitleUiModels() = FlowableTransformer<List<HymnTitle>, List<TitleItem>> { upstream ->
        upstream.map { titles -> titles.map { TitleItem(it.id, it.title) } }
    }

    @VisibleForTesting
    fun getViewState() = FlowableTransformer<List<TitleItem>, Lce<List<TitleItem>>> { upstream ->
        upstream.map { Lce.Content(it) }
    }

    class Factory(private val repository: HymnsRepository, val playlistsRepo: PlaylistsRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HymnListingViewModel(repository, playlistsRepo) as T
        }

    }

}
