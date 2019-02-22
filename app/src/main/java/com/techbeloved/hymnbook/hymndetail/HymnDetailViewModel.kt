package com.techbeloved.hymnbook.hymndetail

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HymnDetailViewModel(private val app: Application, private val hymnRepository: HymnsRepository) : AndroidViewModel(app) {

    private val hymnDetailData: MutableLiveData<Lce<HymnDetailItem>> = MutableLiveData()

    /**
     * This will be monitored by the UI fragment
     */
    val hymnDetailLiveData: LiveData<Lce<HymnDetailItem>>
        get() = hymnDetailData

    private val hymnDetailStateConsumer: Consumer<Lce<HymnDetailItem>> = Consumer {
        Timber.i("Received: $this, $it")
        hymnDetailData.value = it
    }

    private val errorConsumer: Consumer<Throwable> = Consumer {
        Timber.e(it, "Error loading item!")
        hymnDetailData.value = Lce.Error("Error loading item!\n${it.message}")
    }

    private val disposables = CompositeDisposable()
    fun loadHymnDetail(hymnNo: Int) {
        val disposable =
                hymnRepository.getHymnDetailByNumber(hymnNo)
                        .compose(getDetailUiModel())
                        .compose(getDetailUiState())
                        .startWith(Lce.Loading(true))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(hymnDetailStateConsumer, errorConsumer)

        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    /*
     * Use cases - Converting the hymn detail coming from the database to UI model that is suitable for display
     */

    @VisibleForTesting
    fun getDetailUiModel(): FlowableTransformer<HymnDetail, HymnDetailItem> = FlowableTransformer { upstream ->
        upstream.map { detail ->
            HymnDetailItem(
                    detail.num,
                    detail.title,
                    detail.topic,
                    detail.htmlContent
            )
        }
    }

    @VisibleForTesting
    fun getDetailUiState(): FlowableTransformer<HymnDetailItem, Lce<HymnDetailItem>> = FlowableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    @VisibleForTesting
    fun sendLoadingCompleteSignal() = FlowableTransformer<Lce<HymnDetailItem>, Lce<HymnDetailItem>> { upstream ->
        upstream.flatMap { detailLce ->
            when (detailLce) {
                is Lce.Content -> {
                    Flowable.just(detailLce, Lce.Loading(false))
                }
                else -> Flowable.just(detailLce)
            }
        }
    }

    class Factory(private val app: Application, private val repository: HymnsRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HymnDetailViewModel(app, repository) as T
        }

    }

}