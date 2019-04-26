package com.techbeloved.hymnbook.hymndetail

import androidx.annotation.IntDef
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class HymnPagerViewModel(private val repository: HymnsRepository) : ViewModel() {

    private val _hymnIndicesLiveData: MutableLiveData<Lce<List<Int>>> = MutableLiveData()

    val hymnIndicesLiveData: MutableLiveData<Lce<List<Int>>>
        get() = _hymnIndicesLiveData

    private val indicesConsumer: Consumer<in Lce<List<Int>>>? = Consumer {
        _hymnIndicesLiveData.value = it
    }

    private val errorConsumer: Consumer<in Throwable>? = Consumer {
        _hymnIndicesLiveData.value = Lce.Error("Failed to load indices of hymns")
    }

    private val compositeDisposable = CompositeDisposable()
    fun loadHymnIndices(@SortBy sortBy: Int = BY_NUMBER) {
        val disposable = repository.loadHymnIndices(sortBy)
                .compose(indicesToLceMapper())
                .startWith(Lce.Loading(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(indicesConsumer, errorConsumer)

        compositeDisposable.add(disposable)
    }

    private fun indicesToLceMapper(): FlowableTransformer<List<Int>, Lce<List<Int>>> = FlowableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    class Factory(private val provideRepository: HymnsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HymnPagerViewModel(provideRepository) as T
        }

    }
}

const val BY_TITLE = 12
const val BY_NUMBER = 13
const val BY_FAVORITE = 14

@IntDef(BY_TITLE, BY_NUMBER, BY_FAVORITE)
@Retention(AnnotationRetention.SOURCE)
annotation class SortBy
