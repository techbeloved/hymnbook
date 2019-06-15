package com.techbeloved.hymnbook.sheetmusic

import androidx.lifecycle.*
import com.techbeloved.hymnbook.data.repo.OnlineHymn
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.SortBy
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SheetMusicPagerViewModel(private val repo: OnlineRepo) : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val hymnIndicesLiveData: MutableLiveData<Lce<List<Int>>> = MutableLiveData()

    val hymnIndicesLive: LiveData<Lce<List<Int>>>
        get() = Transformations.distinctUntilChanged(hymnIndicesLiveData)

    fun loadIndices(@SortBy sortBy: Int = BY_NUMBER) {
        repo.getAllHymns()
                .compose(sortBy(sortBy))
                .compose(getIndices())
                .compose(contentToLceMapper())
                .startWith(Lce.Loading(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ hymnIndicesLiveData.value = it }, { Timber.w(it, "Error loading indices!") })
                .run { disposables.add(this) }
    }

    private fun getIndices(): ObservableTransformer<List<OnlineHymn>, List<Int>> = ObservableTransformer { upstream ->
        upstream.map { hymns -> hymns.map { it.id } }
    }


    private fun sortBy(sortBy: Int): ObservableTransformer<List<OnlineHymn>, List<OnlineHymn>> = ObservableTransformer { upstream ->
        upstream.map { hymns ->
            when (sortBy) {
                BY_NUMBER -> hymns.sortedBy { it.id }
                else -> hymns.sortedBy { it.title }
            }
        }
    }

    private fun <T> contentToLceMapper(): ObservableTransformer<T, Lce<T>> = ObservableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    class Factory(private val onlineRepo: OnlineRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SheetMusicPagerViewModel(onlineRepo) as T
        }

    }
}
