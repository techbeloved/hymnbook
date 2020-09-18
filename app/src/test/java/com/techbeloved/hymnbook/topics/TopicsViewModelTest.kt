package com.techbeloved.hymnbook.topics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TopicsViewModelTest {

    @Mock
    private lateinit var allTopicsObserver: Observer<Lce<List<TopicItem>>>
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var topicsUseCasesMock: TopicsUseCases
    private lateinit var subject: TopicsViewModel

    @Before
    fun setUp() {
        subject = TopicsViewModel(topicsUseCasesMock)

    }

    @Test
    fun getAllTopics_success_shouldReturnAllAvailableTopics() {
        val topicItems = listOf(
                TopicItem(1, "topic1"),
                TopicItem(2, "topic2")
        )

        whenever(topicsUseCasesMock.topics()).thenReturn(Observable.just(topicItems))
        subject.allTopicsLiveData.observeForever(allTopicsObserver)

        subject.loadTopics()

        inOrder(allTopicsObserver) {
            verify(allTopicsObserver).onChanged(Lce.Loading(true))
            verify(allTopicsObserver).onChanged(Lce.Content(topicItems))
        }
    }

    @Test
    fun getAllTopics_error_shouldReportToTheUIWithError() {
        // Setup
        whenever(topicsUseCasesMock.topics()).thenReturn(Observable.error(Throwable()))
        subject.allTopicsLiveData.observeForever(allTopicsObserver)

        // Execute
        subject.loadTopics()

        // Verify
        inOrder(allTopicsObserver) {
            verify(allTopicsObserver).onChanged(Lce.Loading(true))
            verify(allTopicsObserver).onChanged(Lce.Error("Error loading topics!"))
        }
    }
}