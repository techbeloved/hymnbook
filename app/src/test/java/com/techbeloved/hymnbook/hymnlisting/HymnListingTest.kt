package com.techbeloved.hymnbook.hymnlisting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.inOrder
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.HymnsRepositoryImp
import com.techbeloved.hymnbook.usecases.Lce
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class HymnListingTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mHymnsStateObserver: Observer<Lce<List<HymnTitle>>>
    private val loadedHymns = listOf(HymnTitle(1, "hymn1"),
            HymnTitle(2, "hymn2"),
            HymnTitle(3, "hymn3"))
    private val enableLoading = Lce.Loading<List<HymnTitle>>(true)
    private val disableLoading = Lce.Loading<List<HymnTitle>>(false)

    private val content: Lce.Content<List<HymnTitle>> = Lce.Content(loadedHymns)

    private lateinit var hymnsRepository: HymnsRepository
    private val hymnListingViewModel = HymnListingViewModel(hymnsRepository)

    @Before
    fun setup() {
        hymnsRepository = HymnsRepositoryImp()
    }

    @Test
    fun load_hymn_titles() {

        hymnListingViewModel.hymnTitlesLiveData.observeForever(mHymnsStateObserver)
        hymnListingViewModel.loadHymnTitles()

        val inOrder = inOrder(mHymnsStateObserver)

        inOrder.verify(mHymnsStateObserver).onChanged(enableLoading)
        inOrder.verify(mHymnsStateObserver).onChanged(content)
        inOrder.verify(mHymnsStateObserver).onChanged(disableLoading)
    }
}
