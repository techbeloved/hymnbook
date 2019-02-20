package com.techbeloved.hymnbook.data.repo

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
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
    }


    @Test
    fun get_hymn_titles_from_database() {
        given(db.hymnDao()).willReturn(hymnDao)
        hymnsRepository.loadHymnTitles()
        verify(db.hymnDao()).getAllHymnTitles()
    }

    @Test
    fun getHymnDetailByNumber_loads_only_the_required_hymn_item_with_topic_info() {
        // Setup
        val hymn = Hymn("hymn_1", 1, "hymn1", listOf("verse1", "verse2"))
        given(db.hymnDao()).willReturn(hymnDao)
        //Execute
        hymnsRepository.getHymnDetailByNumber(hymn.num)
        // Verify
        verify(db.hymnDao()).getHymnDetail(hymn.num)
    }
}