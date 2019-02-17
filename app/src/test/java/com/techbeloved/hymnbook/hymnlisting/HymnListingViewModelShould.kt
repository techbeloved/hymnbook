package com.techbeloved.hymnbook.hymnlisting

import androidx.annotation.NonNull
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.ui.TitleItem
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.disposables.Disposable
import io.reactivex.Scheduler
import org.junit.BeforeClass
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


@RunWith(MockitoJUnitRunner::class)
class HymnListingViewModelShould {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var hymnsRepository: HymnsRepository

    val hymnTitlesFromRepo = listOf(HymnTitle(1, "hymn1"),
            HymnTitle(2, "hymn2"),
            HymnTitle(3, "hymn3"))

    private lateinit var hymnListingViewModel: HymnListingViewModel

    @Before
    fun setUp() {
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
        // Setup
        val titlesFlow = Flowable.just(hymnTitlesFromRepo)
        whenever(hymnsRepository.loadHymnTitles()).thenReturn(titlesFlow)

        // Execute
        hymnListingViewModel.loadHymnTitles()

        // Verify
        verify(hymnsRepository).loadHymnTitles()
    }

    @Test
    fun getHymnTitleUiModels_from_hymn_titles() {
        // Setup
        val hymnTitles = listOf<HymnTitle>(HymnTitle(1, "title1"), HymnTitle(2, "title2"))
        val result = listOf(TitleItem(1, "title1"), TitleItem(2, "title2"))

        val titlesFlow = Flowable.just(hymnTitles)
        val titlesTest = TestSubscriber<List<TitleItem>>()

        // Execute
        titlesFlow.compose(hymnListingViewModel.getHymnTitleUiModels())
                .subscribe(titlesTest)

        // Verify
        titlesTest.assertSubscribed()
        titlesTest.assertValue(result)
    }

    @Test
    fun getViewState_from_ui_models() {
        val itemModels = listOf(TitleItem(1, "title1"), TitleItem(2, "title2"))
        val result = Lce.Content(itemModels)

        val itemModelsFlow = Flowable.just(itemModels)
        val modelsTest = TestSubscriber<Lce<List<TitleItem>>>()

        itemModelsFlow.compose(hymnListingViewModel.getViewState())
                .subscribe(modelsTest)
        modelsTest.assertSubscribed()
        modelsTest.assertValue(result)
    }
}