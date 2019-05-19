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
    private var disposableLoadHymn: CompositeDisposable? = CompositeDisposable()
    private val hymnDetailLce: MutableLiveData<Lce<SheetMusicState>> = MutableLiveData()

    private val disposables = CompositeDisposable()

    val hymnDetail: LiveData<Lce<SheetMusicState>>
        get() = hymnDetailLce

    fun loadHymnDetail(hymnNo: Int) {
        // We want to cancel previous subscriptions
        disposableLoadHymn?.dispose()
        disposableLoadHymn = CompositeDisposable()

        hymnUseCases.hymnSheetMusicDetail(hymnNo)
                .compose(contentToLceMapper())
                .distinctUntilChanged()
                .subscribe({ state -> hymnDetailLce.value = state },
                        { throwable ->
                            Timber.w(throwable, "Error getting sheet music detail")
                            hymnDetailLce.value = Lce.Error(throwable.localizedMessage)
                        })
                .run { disposableLoadHymn!!.add(this) }
    }

    fun checkForNewUpdate(hymnNo: Int) {
        hymnUseCases.shouldDownloadUpdatedSheetMusic(hymnNo)
                .subscribe({ updateAvailable ->
                    if (updateAvailable) {
                        hymnUseCases.downloadSheetMusic(hymnNo)
                    }
                }, { Timber.w(it, "Failed to get updates") })
                .let { disposables.add(it) }
    }


    private fun <T> contentToLceMapper(): ObservableTransformer<T, Lce<T>> = ObservableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    override fun onCleared() {
        super.onCleared()
        disposableLoadHymn?.let { if (!it.isDisposed) it.dispose() }
        disposableLoadHymn = null
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