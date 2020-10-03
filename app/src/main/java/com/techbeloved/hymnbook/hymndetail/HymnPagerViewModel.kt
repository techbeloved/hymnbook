package com.techbeloved.hymnbook.hymndetail

import androidx.annotation.IntDef
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.techbeloved.hymnbook.data.ShareLinkProvider
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.playlists.PlaylistsRepo
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.*
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * @param categoryUri holds information about the category we are currently browsing
 */
class HymnPagerViewModel @ViewModelInject constructor(private val repository: HymnsRepository,
                                                      private val playlistsRepo: PlaylistsRepo,
                                                      private val onlineRepo: OnlineRepo,
                                                      @Assisted private val savedStateHandle: SavedStateHandle,
                                                      private val schedulerProvider: SchedulerProvider,
                                                      private val shareLinkProvider: ShareLinkProvider,
                                                      private val preferencesRepo: SharedPreferencesRepo) : ViewModel() {

    private val categoryUri: String get() = savedStateHandle[CATEGORY_URI_ARG]!!
    private val _hymnIndicesLiveData: MutableLiveData<Lce<List<Pair<Int, Boolean>>>> = MutableLiveData()

    private val _shareLinkStatus: MutableLiveData<ShareStatus> = MutableLiveData()
    val shareLinkStatus: LiveData<ShareStatus> get() = _shareLinkStatus

    private val _categoryHeader: MutableLiveData<String> = MutableLiveData()
    val header: LiveData<String> get() = _categoryHeader

    val hymnIndicesLiveData: LiveData<Lce<List<Pair<Int, Boolean>>>>
        get() = Transformations.distinctUntilChanged(_hymnIndicesLiveData)

    private val indicesConsumer: Consumer<in Lce<List<Pair<Int, Boolean>>>>? = Consumer {
        _hymnIndicesLiveData.value = it
    }

    private val errorConsumer: Consumer<in Throwable>? = Consumer {
        _hymnIndicesLiveData.value = Lce.Error("Failed to load indices of hymns")
    }

    private val compositeDisposable = CompositeDisposable()

    init {
        setupHymnDisplayPreference()
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
        indicesObservable.flatMap { localIndices ->
            // Check online hymns and set the boolean variable to true if the given index has online entry.
            // We use this to determine if it's possible to show sheet music.
            onlineRepo.hymnIds(sortBy).map { onlineIndices ->
                localIndices.map { Pair(it, onlineIndices.contains(it)) }
            }
        }
                .compose(indicesToLceMapper())
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

    private fun indicesToLceMapper(): ObservableTransformer<List<Pair<Int, Boolean>>, Lce<List<Pair<Int, Boolean>>>> = ObservableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()

    }

    private var shareDisposable: CompositeDisposable? = null

    /**
     * Requests share link for given hymnId. We dispose the disposable before continuing because this can be called repeatedly
     * by clicking the share menu button
     */
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

    private val _preferSheetMusic: MutableLiveData<Boolean> = MutableLiveData(false)
    val preferSheetMusic: LiveData<Boolean> = _preferSheetMusic

    private fun setupHymnDisplayPreference() {
        preferencesRepo.preferSheetMusic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _preferSheetMusic.value = it }, { Timber.w(it) })
                .also { compositeDisposable.add(it) }
    }

    companion object {
        const val CATEGORY_URI_ARG = "categoryUriArgument"
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