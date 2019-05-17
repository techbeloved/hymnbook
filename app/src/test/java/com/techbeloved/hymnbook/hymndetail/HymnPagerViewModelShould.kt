package com.techbeloved.hymnbook.hymndetail

import androidx.annotation.NonNull
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.repo.HymnsRepository
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
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class HymnPagerViewModelShould {

    @get:Rule
    val rule = InstantTaskExecutorRule()

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
                    return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, false)
                }
            }

            RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
            RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
            RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
            RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediate }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }
        }
    }
    @Mock
    private lateinit var hymnRepository: HymnsRepository
    lateinit var hymnPagerViewModel: HymnPagerViewModel

    @Before
    fun setUp() {
        hymnPagerViewModel = HymnPagerViewModel(hymnRepository)
    }

    @Test
    fun loadHymnIndices_gets_all_hymn_numbers_from_repo_sorted_accordingly() {
        val sorKey = 12
        val listIndices = listOf(1, 2, 3, 4)
        whenever(hymnRepository.loadHymnIndices(anyInt())).thenReturn(Flowable.just(listIndices))

        hymnPagerViewModel.loadHymnIndices(sorKey)

        verify(hymnRepository).loadHymnIndices(sorKey)
    }
}