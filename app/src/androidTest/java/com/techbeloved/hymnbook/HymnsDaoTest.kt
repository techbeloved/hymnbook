package com.techbeloved.hymnbook

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class HymnsDaoTest {
    private lateinit var hymnDao: HymnDao
    private lateinit var db: HymnsDatabase

    private lateinit var hymnList: List<Hymn>

    private val testSubscriber = TestSubscriber<Hymn>()
    private val testListSubscriber = TestSubscriber<List<Hymn>>()
    /**
     * Helps in running background task synchronously. Must be set if using RxJava and LiveData
     */
    @get:Rule
    val rule = InstantTaskExecutorRule()

    val hymn1 = Hymn("hymn_1", 1, "one", listOf("verse1", "verse2"))
    val hymn2 = Hymn("hymn_2", 2, "two", listOf("verse1", "verse2"))
    val hymn3 = Hymn("hymn_3", 3, "three", listOf("verse1", "verse2"))

    @Before
    fun createDb() {
        val context = HymnbookApp.instance.applicationContext
        db = Room.inMemoryDatabaseBuilder(context, HymnsDatabase::class.java).build()
        hymnDao = db.hymnDao()

        hymnList = listOf(hymn1, hymn2, hymn3)

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun write_data_and_read_in_list() {

        hymnDao.insertAll(hymnList)
        val hymn2Flow = hymnDao.getHymnByNumber(2)

        hymn2Flow.subscribe(testSubscriber)
        testSubscriber.assertSubscribed()
        testSubscriber.assertValue(hymn2)
        testSubscriber.dispose()

    }

    @Test
    @Throws(Exception::class)
    fun getAllHymns_sorts_result_according_to_supplied_parameter() {
        hymnDao.insertAll(hymnList)
        val result = hymnDao.getAllHymns()
        result.subscribe(testListSubscriber)
        testListSubscriber.assertSubscribed()
        testListSubscriber.assertValue(hymnList.sortedBy { it.title })
        testListSubscriber.dispose()

    }

    @Test
    @Throws(Exception::class)
    fun getAllHymnTitles_returns_returns_hymn_titles_sorted_properly() {

        val titlesSubscriber = TestSubscriber<List<HymnTitle>>()

        hymnDao.insertAll(hymnList)
        val expected = listOf(HymnTitle(1, "one"),
                HymnTitle(2, "two"), HymnTitle(3, "three"))
        val result = hymnDao.getAllHymnTitles()
        result.subscribe(titlesSubscriber)
        titlesSubscriber.assertSubscribed()
        titlesSubscriber.assertValue(expected)
        titlesSubscriber.dispose()
    }


}