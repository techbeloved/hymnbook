package com.techbeloved.hymnbook.sheetmusic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.SchedulerProvider
import com.techbeloved.hymnbook.utils.schedulers.ImmediateSchedulerProvider
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SheetMusicListingViewModelTest {
    // region constants ----------------------------------------------------------------------------

    private lateinit var titles: List<TitleItem>

    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    @Mock
    private lateinit var hymnUseCases: HymnUseCases

    private lateinit var subject: SheetMusicListingViewModel

    private lateinit var schedulerProvider: SchedulerProvider

    @Mock
    private lateinit var titlesLceObserver: Observer<Lce<List<TitleItem>>>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    // endregion helper fields ---------------------------------------------------------------------

    @Before
    fun setUp() {
        titles = listOf(
                TitleItem(1, "title1", "subtitle1", "description1"),
                TitleItem(2, "title2", "subtitle2", "description2"),
                TitleItem(3, "title3", "subtitle3", "description3")
        )

        schedulerProvider = ImmediateSchedulerProvider()

        subject = SheetMusicListingViewModel(hymnUseCases, schedulerProvider)


    }

    @Test
    fun getHymnTitles_success_returnsAllTitlesWithSheetMusic() {
        // Setup
        whenever(hymnUseCases.hymnSheetMusicTitles()).thenReturn(Observable.just(titles))
        subject.hymnTitlesLce.observeForever(titlesLceObserver)
        // Execute
        subject.loadHymnTitlesFromRepo()

        //Verify

        inOrder(titlesLceObserver) {
            verify(titlesLceObserver).onChanged(Lce.Loading(true))
            verify(titlesLceObserver).onChanged(Lce.Content(titles))
            verifyNoMoreInteractions(titlesLceObserver)
        }
    }
}