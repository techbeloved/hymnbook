package com.techbeloved.hymnbook.hymndetail

import androidx.annotation.IntDef
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.techbeloved.hymnbook.data.ShareLinkProvider
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.data.analytics.AppAnalytics
import com.techbeloved.hymnbook.data.model.HymnNumber
import com.techbeloved.hymnbook.data.model.NewFeature
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.playlists.PlaylistsRepo
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.CATEGORY_PLAYLISTS
import com.techbeloved.hymnbook.utils.DEFAULT_CATEGORY_URI
import com.techbeloved.hymnbook.utils.SchedulerProvider
import com.techbeloved.hymnbook.utils.buildCategoryUri
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * categoryUri holds information about the category we are currently browsing
 */
@HiltViewModel
class HymnPagerViewModel @Inject constructor(
    private val repository: HymnsRepository,
    private val playlistsRepo: PlaylistsRepo,
    savedStateHandle: SavedStateHandle,
    private val schedulerProvider: SchedulerProvider,
    private val shareLinkProvider: ShareLinkProvider,
    private val preferencesRepo: SharedPreferencesRepo,
    private val analytics: AppAnalytics,
) : ViewModel() {

    private val detailArgs = DetailPagerFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _hymnIndicesLiveData: MutableLiveData<Lce<List<HymnNumber>>> = MutableLiveData()

    private val _shareLinkStatus: MutableLiveData<ShareStatus> = MutableLiveData()
    val shareLinkStatus: LiveData<ShareStatus> get() = _shareLinkStatus

    private val _categoryHeader: MutableLiveData<String> = MutableLiveData()
    val header: LiveData<String> get() = _categoryHeader

    val hymnIndicesLiveData: LiveData<Lce<List<HymnNumber>>>
        get() = _hymnIndicesLiveData.distinctUntilChanged()

    private val indicesConsumer: Consumer<in Lce<List<HymnNumber>>> = Consumer {
        _hymnIndicesLiveData.value = it
    }

    private val errorConsumer: Consumer<in Throwable> = Consumer {
        _hymnIndicesLiveData.value = Lce.Error("Failed to load indices of hymns")
    }

    val newFeatures = MutableLiveData<NewFeature?>()


    private val compositeDisposable = CompositeDisposable()

    init {
        setupHymnDisplayPreference()
        loadHymnIndices()
        getCategoryHeader()
        checkNewFeatures()
    }

    private fun checkNewFeatures() {
        preferencesRepo.newFeatures().subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ features ->
                newFeatures.value = features.firstOrNull()
            }, Timber::w)
            .let(compositeDisposable::add)
    }

    fun newFeatureShown(feature: NewFeature) {
        preferencesRepo.shown(feature)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({}, Timber::w).let(compositeDisposable::add)
        analytics.logEvent(data = listOf("NewFeatureShown" to feature.name))
    }

    fun loadHymnIndices(@SortBy sortBy: Int = BY_NUMBER) {

        val categoryId = detailArgs.categoryId
        val indicesObservable = when (detailArgs.category) {
            CATEGORY_PLAYLISTS -> playlistsRepo.loadHymnIndicesInPlaylist(categoryId, sortBy)
            else -> repository.loadHymnIndices(
                sortBy,
                topicId = categoryId
            ).toObservable()
        }
        indicesObservable.compose(indicesToLceMapper())
            .startWith(Lce.Loading(true))
            .observeOn(schedulerProvider.ui())
            .subscribe(indicesConsumer, errorConsumer)
            .let { compositeDisposable.add(it) }
    }

    private fun getCategoryHeader() {
        val category = detailArgs.category
        val categoryId = detailArgs.categoryId

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

    private fun indicesToLceMapper(): ObservableTransformer<List<HymnNumber>, Lce<List<HymnNumber>>> =
        ObservableTransformer { upstream ->
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

        val category = detailArgs.category

        val browsingCategory = if (category == CATEGORY_PLAYLISTS) {
            DEFAULT_CATEGORY_URI
        } else {
            buildCategoryUri(detailArgs.category, detailArgs.categoryId)
        }

        _shareLinkStatus.value = ShareStatus.Loading

        repository.getHymnById(hymnId)
            .firstOrError()
            .flatMap { hymn ->
                shareLinkProvider.getShortLinkForItem(
                    hymn,
                    browsingCategory,
                    description,
                    minimumVersion,
                    logoUrl
                )
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