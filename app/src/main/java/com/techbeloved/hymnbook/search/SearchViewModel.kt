package com.techbeloved.hymnbook.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.data.model.HymnSearch
import com.techbeloved.hymnbook.data.model.SearchResult
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchViewModel(private val repository: HymnsRepository) : ViewModel() {

    private val mutableSearchResults: MutableLiveData<Lce<List<SearchResultItem>>> = MutableLiveData()
    val searchResults: LiveData<Lce<List<SearchResultItem>>>
        get() = mutableSearchResults


    private val searchSubject: PublishProcessor<String> = PublishProcessor.create()

    private val searchItemMapper: FlowableTransformer<List<SearchResult>, List<SearchResultItem>> = FlowableTransformer { upstream ->
        upstream.map { it.map { hymnSearch -> SearchResultItem(hymnSearch.num, hymnSearch.title, hymnSearch.verses[0].substringBefore("\n")) } }
    }

    private val lceMapper: FlowableTransformer<List<SearchResultItem>, Lce<List<SearchResultItem>>> = FlowableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    private val searchConsumer: Consumer<in Lce<List<SearchResultItem>>> = Consumer {
        Timber.i("Received some results? $it")
        mutableSearchResults.value = it
    }

    private val errorConsumer: Consumer<in Throwable> = Consumer {
        Timber.e(it, "Unable to perform search!")
    }

    private val disposables = CompositeDisposable()
    fun search(query: String) {
        searchSubject.onNext(query)
    }

    fun monitorSearch() {
        val disposable = searchSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .switchMap { repository.searchHymns(it) }
                .compose(searchItemMapper)
                .compose(lceMapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchConsumer, errorConsumer)
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    class Factory(private val repository: HymnsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(repository) as T
        }

    }
}
