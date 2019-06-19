package com.techbeloved.hymnbook.sheetmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.SortBy
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class SheetMusicListingViewModel(private val useCases: HymnUseCases, private val schedulerProvider: SchedulerProvider) : ViewModel() {

    private val sortBySubject = PublishSubject.create<Int>()
    private val disposables: CompositeDisposable = CompositeDisposable()
    private val hymnTitlesDataLce: MutableLiveData<Lce<List<TitleItem>>> = MutableLiveData()
    val hymnTitlesLce: LiveData<Lce<List<TitleItem>>>
        get() = hymnTitlesDataLce

    init {
        loadData()
    }

    fun loadHymnTitlesFromRepo(@SortBy sortBy: Int = BY_NUMBER) {
        sortBySubject.onNext(sortBy)
    }

    private fun loadData() {
        sortBySubject.distinctUntilChanged()
                .switchMap { sortBy ->
                    useCases.hymnSheetMusicTitles(sortBy)
                }
                .compose(getViewState())
                .startWith(Lce.Loading(true))
                .observeOn(schedulerProvider.ui())
                .subscribe({ hymnTitlesDataLce.value = it }, { Timber.w(it, "Error!") })
                .run { disposables.add(this) }

    }


    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    private fun <T> getViewState(): ObservableTransformer<T, Lce<T>> = ObservableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    class Factory(private val useCases: HymnUseCases, private val schedulerProvider: SchedulerProvider) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SheetMusicListingViewModel(useCases, schedulerProvider) as T
        }

    }
}