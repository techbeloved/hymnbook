package com.techbeloved.hymnbook.hymndetail

import androidx.annotation.IntDef
import androidx.lifecycle.*
import com.techbeloved.hymnbook.data.ShareLinkProvider
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.playlists.PlaylistsRepo
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.*
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import timber.log.Timber

/**
 * @param categoryUri holds information about the category we are currently browsing
 */
class HymnPagerViewModel(private val repository: HymnsRepository,
                         private val playlistsRepo: PlaylistsRepo,
                         private val categoryUri: String,
                         private val schedulerProvider: SchedulerProvider,
                         private val shareLinkProvider: ShareLinkProvider) : ViewModel() {


    private val _hymnIndicesLiveData: MutableLiveData<Lce<List<Int>>> = MutableLiveData()

    private val _shareLinkStatus: MutableLiveData<ShareStatus> = MutableLiveData()
    val shareLinkStatus: LiveData<ShareStatus> get() = _shareLinkStatus

    private val _categoryHeader: MutableLiveData<String> = MutableLiveData()
    val header: LiveData<String> get() = _categoryHeader

    val hymnIndicesLiveData: LiveData<Lce<List<Int>>>
        get() = Transformations.distinctUntilChanged(_hymnIndicesLiveData)

    private val indicesConsumer: Consumer<in Lce<List<Int>>>? = Consumer {
        _hymnIndicesLiveData.value = it
    }

    private val errorConsumer: Consumer<in Throwable>? = Consumer {
        _hymnIndicesLiveData.value = Lce.Error("Failed to load indices of hymns")
    }

    private val compositeDisposable = CompositeDisposable()

    init {
        loadHymnIndices()
        getCategoryHeader()
    }

    fun loadHymnIndices(@SortBy sortBy: Int = BY_NUMBER) {
        val category = categoryUri.category()
        val categoryId = categoryUri.categoryId()?.toInt() ?: 0
        val indicesObservable = when (category) {
            CATEGORY_PLAYLISTS -> playlistsRepo.loadHymnIndicesInPlaylist(categoryId, sortBy)
            else -> repository.loadHymnIndices(sortBy,
                    topicId = categoryId).toObservable()
        }
        indicesObservable.compose(indicesToLceMapper())
                .startWith(Lce.Loading(true))
                .observeOn(schedulerProvider.ui())
                .subscribe(indicesConsumer, errorConsumer)
                .let { compositeDisposable.add(it) }
    }

    private fun getCategoryHeader() {
        val category = categoryUri.category()
        val categoryId = categoryUri.categoryId()?.toInt() ?: 0

        val titleObservable = when (category) {
            CATEGORY_PLAYLISTS -> playlistsRepo.getPlaylistById(categoryId).map { it.title }
            else -> repository.getTopicById(categoryId).map { it.topic }
        }

        titleObservable.observeOn(schedulerProvider.ui())
                .subscribe({ _categoryHeader.value = it }, {
                    Timber.w(it)
                    _categoryHeader.value = "All Hymns"
                })
                .let { compositeDisposable.add(it) }
    }

    private fun indicesToLceMapper(): ObservableTransformer<List<Int>, Lce<List<Int>>> = ObservableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()

    }

    private var shareDisposable: CompositeDisposable? = null
    fun requestShareLink(hymnId: Int, description: String, minimumVersion: Int, logoUrl: String) {
        shareDisposable?.dispose()
        shareDisposable = CompositeDisposable()

        val category = categoryUri.category()

        val browsingCategory = if (category == CATEGORY_PLAYLISTS) {
            DEFAULT_CATEGORY_URI
        } else {
            categoryUri
        }

        _shareLinkStatus.value = ShareStatus.Loading

        repository.getHymnById(hymnId)
                .firstOrError()
                .flatMap { hymn ->
                    shareLinkProvider.getShortLinkForItem(hymn, browsingCategory, description, minimumVersion, logoUrl)
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

    class Factory(private val provideRepository: HymnsRepository,
                  private val playlistsRepo: PlaylistsRepo,
                  private val categoryUri: String,
                  private val schedulerProvider: SchedulerProvider,
                  private val shareLinkProvider: ShareLinkProvider) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HymnPagerViewModel(provideRepository, playlistsRepo, categoryUri, schedulerProvider, shareLinkProvider) as T
        }

    }
}

const val BY_TITLE = 12
const val BY_NUMBER = 13
const val BY_FAVORITE = 14

@IntDef(BY_TITLE, BY_NUMBER, BY_FAVORITE)
@Retention(AnnotationRetention.SOURCE)
annotation class SortBy


sealed class ShareStatus {
    object Loading : ShareStatus()
    data class Success(val shareLink: String) : ShareStatus()
    data class Error(val error: Throwable) : ShareStatus()
    object None : ShareStatus()
}