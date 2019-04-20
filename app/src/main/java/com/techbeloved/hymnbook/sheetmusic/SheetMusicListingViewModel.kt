package com.techbeloved.hymnbook.sheetmusic

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.repo.OnlineHymn
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SheetMusicListingViewModel(private val repo: OnlineRepo, private val app: Application) : AndroidViewModel(app) {

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

    fun getLatestCatalogue() {
        repo.getLatestCatalogUrl()
                .subscribeOn(Schedulers.io())
                .subscribe({ catalogUrl -> enqueueDownload(catalogUrl) }, { Timber.w(it) })
                .run { disposables.add(this) }
    }

    private fun enqueueDownload(catalogUrl: String) {

        val request = DownloadManager.Request(Uri.parse(catalogUrl))
                .setTitle("Hymns Catalog")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalFilesDir(app, null, "${app.getString(R.string.file_path_downloads)}/wccrm_catalog.zip")
                .setAllowedOverMetered(true)

        val downloadManager: DownloadManager = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Save download id in shared preferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)
        val currentDownloadIdsPref = rxPreferences.getStringSet(app.getString(R.string.pref_key_current_download_id))
        val currentDownloadIds = currentDownloadIdsPref.get()
        currentDownloadIdsPref.set(currentDownloadIds.plus(downloadId.toString()))

        // Update first start flag
        val isFirstStart: Preference<Boolean> = rxPreferences.getBoolean(app.getString(R.string.pref_key_first_start), true)
        isFirstStart.set(false)

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

    class Factory(private val repository: OnlineRepo, val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SheetMusicListingViewModel(repository, app) as T
        }

    }
}