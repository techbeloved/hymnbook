package com.techbeloved.hymnbook.hymnlisting

import androidx.annotation.NonNull
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.model.HymnTitle
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
class HymnListingTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mHymnsStateObserver: Observer<Lce<List<TitleItem>>>
    private val loadedHymns = listOf(TitleItem(1, "hymn1"),
            TitleItem(2, "hymn2"),
            TitleItem(3, "hymn3"))
    private val enableLoading = Lce.Loading<List<TitleItem>>(true)
    private val disableLoading = Lce.Loading<List<TitleItem>>(false)

    private val content: Lce.Content<List<TitleItem>> = Lce.Content(loadedHymns)

    private lateinit var hymnsRepository: HymnsRepository
    private lateinit var hymnListingViewModel:HymnListingViewModel

    @Mock
    private lateinit var hymnDatabase: HymnsDatabase
    @Mock
    private lateinit var hymnDao: HymnDao

    @Before
    fun setup() {

        hymnsRepository = HymnsRepositoryImp(hymnDatabase)
        hymnListingViewModel = HymnListingViewModel(hymnsRepository)
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
    fun load_hymn_titles() {
        val hymnTitles = listOf(HymnTitle(1, "hymn1"),
                HymnTitle(2, "hymn2"),
                HymnTitle(3, "hymn3"))
        val titlesFlow = Flowable.just(hymnTitles)

        whenever(hymnDatabase.hymnDao()).thenReturn(hymnDao)
        whenever(hymnDao.getAllHymnTitles()).thenReturn(titlesFlow)

        hymnListingViewModel.hymnTitlesLiveData.observeForever(mHymnsStateObserver)
        hymnListingViewModel.loadHymnTitles()

        val inOrder = inOrder(mHymnsStateObserver)

        inOrder.verify(mHymnsStateObserver).onChanged(enableLoading)
        inOrder.verify(mHymnsStateObserver).onChanged(content)
        //inOrder.verify(mHymnsStateObserver).onChanged(disableLoading)
    }
}
