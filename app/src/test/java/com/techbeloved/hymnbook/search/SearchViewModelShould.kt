package com.techbeloved.hymnbook.search

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchViewModelShould {

    private lateinit var searchViewModel: SearchViewModel

    @Mock
    private lateinit var hymnsRepository: HymnsRepository

    @Before
    fun setUp() {
        searchViewModel = SearchViewModel(hymnsRepository)
    }


    @Test
    fun search_hymns_by_calling_the_repository_and_observing_the_results_observable() {
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

        // Execute
        searchViewModel.search(searchTerm)

        // Verify

        // Using publisher. Not yet tested
        //verify(hymnsRepository).searchHymns(searchTerm)
    }
}