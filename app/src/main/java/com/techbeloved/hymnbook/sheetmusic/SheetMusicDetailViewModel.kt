package com.techbeloved.hymnbook.sheetmusic

import android.app.Application
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.repo.OnlineHymn
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

class SheetMusicDetailViewModel(private val repo: OnlineRepo, val app: Application) : AndroidViewModel(app) {
    private var disposables: CompositeDisposable? = CompositeDisposable()
    private val hymnDetailLce: MutableLiveData<Lce<OnlineHymn>> = MutableLiveData()

    val hymnDetail: LiveData<Lce<OnlineHymn>>
        get() = hymnDetailLce

    fun loadHymnDetail(hymnNo: Int) {
        // We want to cancel previous subscriptions
        disposables?.dispose()
        disposables = CompositeDisposable()


        val preferences = PreferenceManager.getDefaultSharedPreferences(app)
        val rxPreferences = RxSharedPreferences.create(preferences)
        val hymnCatalogReadyPref = rxPreferences.getBoolean(app.getString(R.string.pref_key_hymn_catalog_ready), false)

        hymnCatalogReadyPref.asObservable()
                .filter { it } // Only Proceed when catalog has been downloaded and unzipped
                .switchMap { repo.getHymnById(hymnNo) }
                .compose(getLocalFilesUrls())
                .compose(contentToLceMapper())
                .startWith(Lce.Loading(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ hymnDetailLce.value = it }, { error ->
                    Timber.w(error, "Error getting hymn detail")
                    hymnDetailLce.value = Lce.Error("Error loading hymn detail! Touch to retry")
                })
                .run { disposables?.add(this) }
    }


    private fun getLocalFilesUrls(): ObservableTransformer<OnlineHymn, OnlineHymn> = ObservableTransformer { upstream ->
        upstream.map { hymn ->
            val localFileUrl = File(app.getExternalFilesDir(null), app.getString(R.string.file_path_catalogs) + "hymn_${hymn.id}.pdf")
            if (localFileUrl.exists()) {
                OnlineHymn(hymn.id, hymn.title, localFileUrl.absolutePath, isDownloaded = true)
            } else {
                OnlineHymn(hymn.id, hymn.title, hymn.sheetMusicUrl)
            }
        }
    }

    private fun <T> contentToLceMapper(): ObservableTransformer<T, Lce<T>> = ObservableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    override fun onCleared() {
        super.onCleared()
        disposables?.let { if (!it.isDisposed) it.dispose() }
        disposables = null
    }

    class Factory(private val repository: OnlineRepo, private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SheetMusicDetailViewModel(repository, application) as T
        }

    }
}