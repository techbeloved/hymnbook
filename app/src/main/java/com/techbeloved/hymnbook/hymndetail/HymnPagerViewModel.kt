package com.techbeloved.hymnbook.hymndetail

import androidx.annotation.IntDef
import androidx.lifecycle.*
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.playlists.PlaylistsRepo
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.CATEGORY_PLAYLISTS
import com.techbeloved.hymnbook.utils.SchedulerProvider
import com.techbeloved.hymnbook.utils.category
import com.techbeloved.hymnbook.utils.categoryId
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
                         private val schedulerProvider: SchedulerProvider) : ViewModel() {


    private val _hymnIndicesLiveData: MutableLiveData<Lce<List<Int>>> = MutableLiveData()

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

    class Factory(private val provideRepository: HymnsRepository,
                  private val playlistsRepo: PlaylistsRepo,
                  private val categoryUri: String,
                  private val schedulerProvider: SchedulerProvider) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HymnPagerViewModel(provideRepository, playlistsRepo, categoryUri, schedulerProvider) as T
        }

    }
}

const val BY_TITLE = 12
const val BY_NUMBER = 13
const val BY_FAVORITE = 14

@IntDef(BY_TITLE, BY_NUMBER, BY_FAVORITE)
@Retention(AnnotationRetention.SOURCE)
annotation class SortBy

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
