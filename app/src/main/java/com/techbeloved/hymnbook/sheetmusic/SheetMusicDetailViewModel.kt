package com.techbeloved.hymnbook.sheetmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techbeloved.hymnbook.usecases.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SheetMusicDetailViewModel @Inject constructor(private val hymnUseCases: HymnUseCases) : ViewModel() {
    private var disposableLoadHymn: CompositeDisposable? = CompositeDisposable()
    private val hymnDetailLce: MutableLiveData<Lce<SheetMusicState>> = MutableLiveData()

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
                            hymnDetailLce.value = Lce.Error(throwable.localizedMessage
                                    ?: "Error occurred")
                        })
                .run { disposableLoadHymn!!.add(this) }
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
}