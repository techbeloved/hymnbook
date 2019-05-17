package com.techbeloved.hymnbook.sheetmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class SheetMusicDetailViewModel(private val hymnUseCases: HymnUseCases) : ViewModel() {
    private var disposables: CompositeDisposable? = CompositeDisposable()
    private val hymnDetailLce: MutableLiveData<Lce<SheetMusicState>> = MutableLiveData()

    val hymnDetail: LiveData<Lce<SheetMusicState>>
        get() = hymnDetailLce

    fun loadHymnDetail(hymnNo: Int) {
        // We want to cancel previous subscriptions
        disposables?.dispose()
        disposables = CompositeDisposable()

        hymnUseCases.hymnSheetMusicDetail(hymnNo)
                .compose(contentToLceMapper())
                .distinctUntilChanged()
                .subscribe({ state -> hymnDetailLce.value = state },
                        { throwable ->
                            Timber.w(throwable, "Error getting sheet music detail")
                            hymnDetailLce.value = Lce.Error(throwable.localizedMessage)
                        })
                .run { disposables!!.add(this) }
    }


    private fun <T> contentToLceMapper(): ObservableTransformer<T, Lce<T>> = ObservableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    override fun onCleared() {
        super.onCleared()
        disposables?.let { if (!it.isDisposed) it.dispose() }
        disposables = null
    }

    fun download(hymnId: Int) {
        hymnUseCases.downloadSheetMusic(hymnId)
    }

    class Factory(private val hymnUseCases: HymnUseCases) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SheetMusicDetailViewModel(hymnUseCases) as T
        }

    }
}