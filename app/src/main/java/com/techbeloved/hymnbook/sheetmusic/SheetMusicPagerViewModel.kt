package com.techbeloved.hymnbook.sheetmusic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.techbeloved.hymnbook.data.ShareLinkProvider
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.repo.OnlineHymn
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.ShareStatus
import com.techbeloved.hymnbook.hymndetail.SortBy
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.DEFAULT_SHEET_MUSIC_CATEGORY
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SheetMusicPagerViewModel @ViewModelInject constructor(private val repo: OnlineRepo,
                                                            private val shareLinkProvider: ShareLinkProvider,
                                                            private val schedulerProvider: SchedulerProvider) : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val hymnIndicesLiveData: MutableLiveData<Lce<List<Int>>> = MutableLiveData()

    private val _shareLinkStatus: MutableLiveData<ShareStatus> = MutableLiveData()
    val shareLinkStatus: LiveData<ShareStatus> get() = _shareLinkStatus

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

    private var shareDisposable: CompositeDisposable? = null

    fun requestShareLink(hymnId: Int,
                         description: String,
                         minimumVersion: Int,
                         shareLogoUrl: String,
                         categoryUri: String = DEFAULT_SHEET_MUSIC_CATEGORY) {

        shareDisposable?.dispose()
        shareDisposable = CompositeDisposable()



        _shareLinkStatus.value = ShareStatus.Loading

        repo.getHymnById(hymnId)
                .firstOrError()
                .map { Hymn("hymn_${it.id}", it.id, it.title, emptyList(), it.title) }
                .flatMap { hymn ->
                    shareLinkProvider.getShortLinkForItem(hymn, categoryUri, description, minimumVersion, shareLogoUrl)
                            .subscribeOn(schedulerProvider.io())
                }
                .map { ShareStatus.Success(it) }.cast(ShareStatus::class.java)
                .observeOn(schedulerProvider.ui())
                .subscribe({ shareStatus ->
                    _shareLinkStatus.value = shareStatus
                    _shareLinkStatus.value = ShareStatus.None
                }, {
                    _shareLinkStatus.value = ShareStatus.Error(it)
                    _shareLinkStatus.value = ShareStatus.None
                })
                .let { shareDisposable?.add(it) }


    }
}
