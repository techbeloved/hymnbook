package com.techbeloved.hymnbook.hymndetail

import android.app.Application
import androidx.annotation.NonNull
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.HymnsRepositoryImp
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class HymnDetailFeature {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var hymnDetailStateObserver: Observer<Lce<HymnDetailItem>>
    @Mock
    private lateinit var hymnIndicesObserver: Observer<in Lce<List<Int>>>

    @Mock
    private lateinit var hymnDatabase: HymnsDatabase
    @Mock
    private lateinit var hymnDao: HymnDao
    @Mock
    private lateinit var app: Application
    private lateinit var hymnsRepository: HymnsRepository
    private lateinit var detailViewModel: HymnDetailViewModel

    private lateinit var detailPagerViewModel: HymnPagerViewModel

    private val enableLoading = Lce.Loading<HymnDetailItem>(true)
    private val disableLoading = Lce.Loading<HymnDetailItem>(false)

    @Before
    fun setUp() {
        hymnsRepository = HymnsRepositoryImp.getInstance(hymnDatabase)
        detailViewModel = HymnDetailViewModel(app, hymnsRepository)

        detailPagerViewModel = HymnPagerViewModel(hymnsRepository)

        whenever(hymnDatabase.hymnDao()).thenReturn(hymnDao)
    }

    // https://stackoverflow.com/questions/43356314/android-rxjava-2-junit-test-getmainlooper-in-android-os-looper-not-mocked-runt
    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpRxSchedulers() {

            val immediate = object : Scheduler() {
                override fun scheduleDirect(@NonNull run: Runnable, delay: Long, @NonNull unit: TimeUnit): Disposable {
                    // this prevents StackOverflowErrors when scheduling with a delay
                    return super.scheduleDirect(run, 0, unit)
                }

                override fun createWorker(): Worker {
                    return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
                }
            }

            RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
            RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
            RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
            RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediate }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }
        }
    }


    @Test
    fun load_hymn_detail() {
        // Setup
        val hymn = HymnDetail("hymn_1", 1, "hymn1", listOf("verse1", "verse2"), "topic")
        val hymnDetailItem = HymnDetailItem(hymn.num, hymn.title, hymn.topic, hymn.htmlContent)
        val expectedUiContent = Lce.Content(hymnDetailItem)
        val hymnFlow = Flowable.just(hymn)

        whenever(hymnDao.getHymnDetail(hymn.num)).thenReturn(hymnFlow)

        // Execution
        detailViewModel.hymnDetailLiveData.observeForever(hymnDetailStateObserver)
        detailViewModel.loadHymnDetail(hymn.num)

        // Verification

        val inOrder = inOrder(hymnDetailStateObserver)

        inOrder.verify(hymnDetailStateObserver).onChanged(enableLoading)
        inOrder.verify(hymnDetailStateObserver).onChanged(expectedUiContent)
    }


    @Test
    fun load_hymn_indices_sorted_by_number() {
        val loading = Lce.Loading<List<Int>>(true)
        val finishedLoading = Lce.Loading<List<Int>>(false)

        val hymnNos = listOf(1, 2, 3, 4, 5, 6, 7)
        val expectedContent = Lce.Content(hymnNos)
        val hymnNosFlow = Flowable.just(hymnNos)

        whenever(hymnDao.getIndicesByNumber()).thenReturn(hymnNosFlow)

        // Execution
        detailPagerViewModel.hymnIndicesLiveData.observeForever(hymnIndicesObserver)
        detailPagerViewModel.loadHymnIndices(BY_NUMBER)

        // Verification

        val inOrder = inOrder(hymnIndicesObserver)

        inOrder.verify(hymnIndicesObserver).onChanged(loading)
        inOrder.verify(hymnIndicesObserver).onChanged(expectedContent)
    }

    @Test
    @Throws(Exception::class)
    fun loading_hymn_indices_by_unsorported_sort_key_returns_error() {
        val loading = Lce.Loading<List<Int>>(true)
        val finishedLoading = Lce.Loading<List<Int>>(false)

        val expectedError = Lce.Error<List<Int>>("Failed to load indices of hymns")
        val wrongSortKey = -1


        // Execution
        detailPagerViewModel.hymnIndicesLiveData.observeForever(hymnIndicesObserver)
        detailPagerViewModel.loadHymnIndices(wrongSortKey)

        // Verification

        val inOrder = inOrder(hymnIndicesObserver)

        inOrder.verify(hymnIndicesObserver).onChanged(loading)
        inOrder.verify(hymnIndicesObserver).onChanged(expectedError)
    }
}