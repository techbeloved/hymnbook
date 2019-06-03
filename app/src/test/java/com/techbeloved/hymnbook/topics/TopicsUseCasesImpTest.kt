package com.techbeloved.hymnbook.topics

import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.utils.SchedulerProvider
import com.techbeloved.hymnbook.utils.schedulers.ImmediateSchedulerProvider
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TopicsUseCasesImpTest {

    private lateinit var schedulerProvider: SchedulerProvider
    @Mock
    private lateinit var hymnRepositoryMock: HymnsRepository
    private lateinit var subject: TopicsUseCasesImp

    @Before
    fun setUp() {
        schedulerProvider = ImmediateSchedulerProvider()
        subject = TopicsUseCasesImp(hymnRepositoryMock, schedulerProvider)

    }

    @Test
    fun loadTopics_success_shouldLoadAllTopicsFromRepository() {
        val topics = listOf(
                Topic(1, "topic1"),
                Topic(2, "topic2")
        )
        whenever(hymnRepositoryMock.loadAllTopics()).thenReturn(Observable.just(topics))

        val testObserver = TestObserver<List<TopicItem>>()

        subject.topics().subscribe(testObserver)

        testObserver.assertSubscribed()
        testObserver.assertNoErrors()
        testObserver.assertValueAt(0) { items -> items[0].id == 1 }
    }
}