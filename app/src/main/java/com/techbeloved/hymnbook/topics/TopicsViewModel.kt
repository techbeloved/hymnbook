package com.techbeloved.hymnbook.topics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class TopicsViewModel(private val topicsUseCases: TopicsUseCases) : ViewModel() {


    private val disposables: CompositeDisposable = CompositeDisposable()
    private val _allTopicsLce: MutableLiveData<Lce<List<TopicItem>>> = MutableLiveData(Lce.Loading(true))
    val allTopicsLiveData: LiveData<Lce<List<TopicItem>>>
        get() = _allTopicsLce

    fun loadTopics() {
        topicsUseCases.topics()
                .subscribe({ topics -> _allTopicsLce.value = Lce.Content(topics) },
                        { throwable ->
                            Timber.w(throwable)
                            _allTopicsLce.value = Lce.Error("Error loading topics!")
                        })
                .let { disposables.add(it) }
    }

    // Load topics already
    init {
        loadTopics()
    }

    private fun <T> getViewState() = ObservableTransformer<T, Lce<T>> { upstream ->
        upstream.map { Lce.Content(it) }
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    class Factory(private val topicsUseCases: TopicsUseCases) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TopicsViewModel(topicsUseCases) as T
        }

    }
}
