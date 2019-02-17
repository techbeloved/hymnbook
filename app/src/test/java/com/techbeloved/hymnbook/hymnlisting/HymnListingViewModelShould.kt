package com.techbeloved.hymnbook.hymnlisting

import com.nhaarman.mockitokotlin2.verify
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HymnListingViewModelShould {

    @Mock
    private lateinit var hymnsRepository: HymnsRepository

    val testSubscriber = TestSubscriber<Lce<List<HymnTitle>>>()

    private lateinit var hymnListingViewModel: HymnListingViewModel

    @Before
    fun setUp() {
        hymnListingViewModel = HymnListingViewModel(hymnsRepository)
    }

    @Test
    fun load_hymn_titles() {
        hymnListingViewModel.loadHymnTitles()
        verify(hymnsRepository).loadHymnTitles()
    }
}