package com.techbeloved.hymnbook.topics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techbeloved.hymnbook.usecases.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TopicsViewModel @Inject constructor(private val topicsUseCases: TopicsUseCases) : ViewModel() {


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

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }
}
