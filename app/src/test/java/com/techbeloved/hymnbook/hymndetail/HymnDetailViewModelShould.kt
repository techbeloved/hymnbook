package com.techbeloved.hymnbook.hymndetail

import android.app.Application
import androidx.annotation.NonNull
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subscribers.TestSubscriber
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Before

import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class HymnDetailViewModelShould {

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


    @Mock
    private lateinit var hymnRepository: HymnsRepository
    @Mock
    lateinit var app: Application

    lateinit var hymnDetailViewModel: HymnDetailViewModel

    @Before
    fun setUp() {
        hymnDetailViewModel = HymnDetailViewModel(app, hymnRepository)
    }

    @Test
    fun getHymnDetail_loads_the_hymn_detail_using_the_specified_id() {
        // Setup
        val hymn = HymnDetail("hymn_1", 1, "hymn1", listOf("verse1", "verse2"), "topic1")
        val hymnFlow = Flowable.just(hymn)

        whenever(hymnRepository.getHymnDetailByNumber(hymn.num)).thenReturn(hymnFlow)

        // Execute
        hymnDetailViewModel.loadHymnDetail(hymn.num)

        // Verify
        verify(hymnRepository).getHymnDetailByNumber(hymn.num)
    }

    @Test
    @Throws(Exception::class)
    fun getDetailUiModel_transforms_hymn_detail_from_repo_to_appropriate_ui_model() {

        // Setup
        val detail1 = HymnDetail("hymn_1", 1, "one", listOf("verse1", "verse2"), "topic1")
        val detailFlow = Flowable.just(detail1)

        val expected = HymnDetailItem(detail1.num,
                detail1.title, detail1.topic, detail1.htmlContent)

        // Execute
        val result = detailFlow.compose(hymnDetailViewModel.getDetailUiModel())
        val detailTest = TestSubscriber<HymnDetailItem>()

        result.subscribe(detailTest)

        //assertThat(result, `is`(com.nhaarman.mockitokotlin2.any<Flowable<HymnDetailItem>>()))

        // Verify
        detailTest.assertSubscribed()
        detailTest.assertValue(expected)
        detailTest.dispose()

    }

    @Test
    @Throws(Exception::class)
    fun getDetailViewState_from_ui_model() {
        // Setup
        val detail1 = HymnDetailItem(1, "hymn_1",  "hymn1", "content of hymn1")
        val expected = Lce.Content(detail1)

        val detailFlow = Flowable.just(detail1)

        // Execute
        val testDetailStateSubscriber = TestSubscriber<Lce<HymnDetailItem>>()
        val result = detailFlow.compose(hymnDetailViewModel.getDetailUiState())
                .subscribe(testDetailStateSubscriber)

        // Verify
        testDetailStateSubscriber.assertSubscribed()
        testDetailStateSubscriber.assertValue(expected)
        testDetailStateSubscriber.dispose()

    }

}