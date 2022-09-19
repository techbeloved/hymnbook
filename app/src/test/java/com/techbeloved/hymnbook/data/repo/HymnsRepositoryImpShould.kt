package com.techbeloved.hymnbook.data.repo

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnNumber
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.BY_TITLE
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HymnsRepositoryImpShould {

    @Mock
    private lateinit var hymnDao: HymnDao

    @Mock
    private lateinit var db: HymnsDatabase

    lateinit var hymnsRepository: HymnsRepository

    @Before
    fun setUp() {
        hymnsRepository = HymnsRepositoryImp(db)
        given(db.hymnDao()).willReturn(hymnDao)
    }


    @Test
    fun get_hymn_titles_from_database() {
        hymnsRepository.loadHymnTitles(BY_NUMBER)
        verify(db.hymnDao()).getAllHymnTitles()
    }

    @Test
    fun getHymnDetailByNumber_loads_only_the_required_hymn_item_with_topic_info() {
        // Setup
        val hymn = Hymn("hymn_1", 1, "hymn1", listOf("verse1", "verse2"), "verse1")
        //Execute
        hymnsRepository.getHymnDetailByNumber(hymn.num)
        // Verify
        verify(db.hymnDao()).getHymnDetail(hymn.num)
    }

    @Test
    fun loadHymnIndices_loads_indices_sorted_according_titles() {
        hymnsRepository.loadHymnIndices(BY_TITLE)
        verify(db.hymnDao()).getIndicesByTitle()
    }

    @Test
    fun loadHymnIndices_loads_indices_sorted_according_hymn_number() {
        hymnsRepository.loadHymnIndices(BY_NUMBER)
        verify(db.hymnDao()).getIndicesByNumber()
    }

    @Test
    fun loadHymnIndices_throws_exception_when_given_a_wrong_sorting_term() {
        val wrongSortBy = -1

        val testSubscriber = TestSubscriber<List<HymnNumber>>()

        hymnsRepository.loadHymnIndices(wrongSortBy).subscribe(testSubscriber)

        testSubscriber.assertSubscribed()
        testSubscriber.assertError(Throwable::class.java)
        testSubscriber.dispose()
    }

    @Test
    fun searchHymns_by_querying_database() {
        // Setup
        val searchTerm = "glo"
        hymnsRepository.searchHymns(searchTerm)
        verify(db.hymnDao()).searchHymns("$searchTerm*")
    }
}