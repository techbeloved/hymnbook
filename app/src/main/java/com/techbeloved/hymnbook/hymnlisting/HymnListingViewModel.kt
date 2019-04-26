package com.techbeloved.hymnbook.hymnlisting

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.hymndetail.SortBy
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers


class HymnListingViewModel(private val hymnsRepository: HymnsRepository) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _hymnTitlesLiveData = MutableLiveData<Lce<List<TitleItem>>>()
    val hymnTitlesLiveData: LiveData<Lce<List<TitleItem>>>
        get() = _hymnTitlesLiveData

    private val sortByProcessor: BehaviorProcessor<Int> = BehaviorProcessor.create()

    init {
        loadHymnsFromDatabase()
    }

    /**
     * Loads hymn titles sorted by the specified term. This is usually called from the fragment
     * at start and each time the sorting criteria changes
     */
    fun loadHymnTitles(@SortBy sortBy: Int) {
        sortByProcessor.onNext(sortBy)
    }

    private fun loadHymnsFromDatabase() {
        val disposable = sortByProcessor
                .distinctUntilChanged()
                .switchMap { sortBy ->
                    hymnsRepository.loadHymnTitles(sortBy)
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

    class Factory(private val repository: HymnsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HymnListingViewModel(repository) as T
        }

    }

}
