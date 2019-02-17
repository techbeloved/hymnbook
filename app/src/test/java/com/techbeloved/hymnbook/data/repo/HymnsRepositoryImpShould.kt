package com.techbeloved.hymnbook.data.repo

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
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
}