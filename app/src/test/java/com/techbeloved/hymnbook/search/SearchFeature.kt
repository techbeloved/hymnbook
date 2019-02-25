package com.techbeloved.hymnbook.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.HymnsRepositoryImp
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.usecases.Lce
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchFeature {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var searchViewModel: SearchViewModel

    private lateinit var repository: HymnsRepository

    @Mock
    private lateinit var hymnDatabase: HymnsDatabase

    @Mock
    private lateinit var hymnDao: HymnDao

    @Before
    fun setUp() {
        repository = HymnsRepositoryImp(hymnDatabase)
        searchViewModel = SearchViewModel(repository)

        whenever(hymnDatabase.hymnDao()).thenReturn(hymnDao)
    }


    @Mock
    private lateinit var searchResultsObserver: Observer<in Lce<List<SearchResultItem>>>

    @Test
    @Ignore // Temporarily
    @Throws(Exception::class)
    fun search_hymns() {
        // Setup
        val vs1 = listOf("glorious one", "power and grace")
        val vs2 = listOf("powerful one", "good and grace")
        val vs3 = listOf("gloriously anointed by", "anointing and love")
        val hymnList = listOf(
                Hymn("hymn_1", 1, "glory",vs1,  vs1[0]),
                Hymn("hymn_2", 2, "power", vs2, vs2[0]),
                Hymn("hymn_3", 3, "anointed", vs3, vs3[0])
        )

        val searchTerm = "glo"

        val expected = hymnList.filter {
            it.title.contains(searchTerm, true)
                    || it.verses[0].contains(searchTerm, true)
        }.map {
            SearchResultItem(it.num, it.title, it.chorus?.substringBefore("\n") ?: it.verses[0].substringBefore("\n"))
        }

        val expectedContent = Lce.Content(expected)
        val searchLoading = Lce.Loading<List<SearchResultItem>>(true)

        // Execute
        searchViewModel.searchResults.observeForever(searchResultsObserver)
        searchViewModel.search(searchTerm)

        // Verify
        verify(searchResultsObserver).onChanged(searchLoading)
        verify(searchResultsObserver).onChanged(expectedContent)
    }
}