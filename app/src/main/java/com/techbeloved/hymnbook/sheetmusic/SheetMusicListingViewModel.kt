package com.techbeloved.hymnbook.sheetmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techbeloved.hymnbook.data.repo.OnlineHymn
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class SheetMusicListingViewModel(private val repo: OnlineRepo) : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val hymnTitlesDataLce: MutableLiveData<Lce<List<TitleItem>>> = MutableLiveData()
    val hymnTitlesLce: LiveData<Lce<List<TitleItem>>>
        get() = hymnTitlesDataLce

    fun loadHymnTitlesFromRepo() {
        repo.getAllHymns()
                .compose(convertToTitleModels())
                .compose(getViewState())
                .startWith(Lce.Loading(true))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ hymnTitlesDataLce.value = it }, { Timber.w(it, "Error!") })
                .run { disposables.add(this) }

    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    private fun convertToTitleModels(): ObservableTransformer<List<OnlineHymn>, List<TitleItem>> = ObservableTransformer { upstream ->
        upstream.map { hymns -> hymns.map { TitleItem(it.id, it.title) } }
    }

    private fun <T> getViewState(): ObservableTransformer<T, Lce<T>> = ObservableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    class Factory(private val repository: OnlineRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SheetMusicListingViewModel(repository) as T
        }

    }
}