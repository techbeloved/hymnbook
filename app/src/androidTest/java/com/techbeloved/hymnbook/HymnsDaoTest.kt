package com.techbeloved.hymnbook

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.techbeloved.hymnbook.data.model.*
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.data.repo.local.TopicDao
import io.reactivex.subscribers.TestSubscriber
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class HymnsDaoTest {
    private lateinit var hymnDao: HymnDao
    private lateinit var topicDao: TopicDao
    private lateinit var db: HymnsDatabase

    private lateinit var hymnList: List<Hymn>
    private lateinit var topicList: List<Topic>

    private val testSubscriber = TestSubscriber<Hymn>()
    private val testListSubscriber = TestSubscriber<List<Hymn>>()

    private val testTopicsSubscriber = TestSubscriber<List<Topic>>()
    private val testTopicSingleSubscriber = TestSubscriber<Topic>()

    private val testHymnDetailSubscriber = TestSubscriber<HymnDetail>()
    /**
     * Helps in running background task synchronously. Must be set if using RxJava and LiveData
     */
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val vs1 = listOf("glorious one", "power and grace")
    private val vs2 = listOf("powerful one", "good and grace")
    private val vs3 = listOf("gloriously anointed by", "anointing and love")
    private val hymn1 = Hymn("hymn_1", 1, "glory", vs1, vs1[0])
    private val hymn2 = Hymn("hymn_2", 2, "power", vs2, vs2[0])
    private val hymn3 = Hymn("hymn_3", 3, "anointed", vs3, vs3[0])

    private val topic1 = Topic(1, "topic1")
    private val topic2 = Topic(2, "topic2")
    private val topic3 = Topic(3, "topic3")

    private val detail1 = HymnDetail(hymn1.id, hymn1.num, hymn1.title, hymn1.verses, topic1.topic)
    private val detail2 = HymnDetail(hymn2.id, hymn2.num, hymn2.title, hymn2.verses, topic2.topic)
    private val detail3 = HymnDetail(hymn3.id, hymn3.num, hymn3.title, hymn3.verses, topic3.topic)

    @Before
    fun createDb() {
        val context = HymnbookApp.instance.applicationContext
        db = Room.inMemoryDatabaseBuilder(context, HymnsDatabase::class.java).build()
        hymnDao = db.hymnDao()
        topicDao = db.topicDao()

        hymn1.topicId = 1
        hymn2.topicId = 1
        hymn3.topicId = 3

        hymnList = listOf(hymn1, hymn2, hymn3)
        topicList = listOf(topic1, topic2, topic3)

        hymnDao.insertAll(hymnList)
        topicDao.insertAll(topicList)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun write_data_and_read_in_list() {

        val hymn2Flow = hymnDao.getHymnByNumber(2)

        hymn2Flow.subscribe(testSubscriber)
        testSubscriber.assertSubscribed()
        testSubscriber.assertValue(hymn2)
        testSubscriber.dispose()

    }

    @Test
    @Throws(Exception::class)
    fun getAllHymns_sorts_result_according_to_supplied_parameter() {
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

        val expected = listOf(HymnTitle(hymn1.num, hymn1.title),
                HymnTitle(hymn2.num, hymn2.title), HymnTitle(hymn3.num, hymn3.title))
        val result = hymnDao.getAllHymnTitles()
        result.subscribe(titlesSubscriber)
        titlesSubscriber.assertSubscribed()
        titlesSubscriber.assertValue(expected)
        titlesSubscriber.dispose()
    }


    @Test
    @Throws(Exception::class)
    fun insert_topics_and_read_in_topic_list() {
        val topicsFlow = topicDao.getAllTopics()

        topicsFlow.subscribe(testTopicsSubscriber)
        testTopicsSubscriber.assertSubscribed()
        testTopicsSubscriber.assertValue(topicList)
        testTopicsSubscriber.dispose()
    }

    @Test
    @Throws(Exception::class)
    fun insert_topics_and_read_in_single_topic() {
        val topicsFlow = topicDao.getTopicById(topic1.id)

        topicsFlow.subscribe(testTopicSingleSubscriber)

        testTopicSingleSubscriber.assertSubscribed()
        testTopicSingleSubscriber.assertValue(topic1)
        testTopicSingleSubscriber.dispose()
    }

    @Test
    @Throws(Exception::class)
    fun get_hymn_detail_with_correct_topic_info() {
        val detailFlow = hymnDao.getHymnDetail(hymn2.num)

        val expected = detail2.copy(topic = topic1.topic)

        detailFlow.subscribe(testHymnDetailSubscriber)
        testHymnDetailSubscriber.assertSubscribed()
        testHymnDetailSubscriber.assertValue(expected)
        testHymnDetailSubscriber.dispose()
    }

    @Test
    @Throws(Exception::class)
    fun get_hymn_indices_sorted_according_to_number() {
        // Setup
        val expectedIndices = hymnList.sortedBy { it.num }.map { it.num }

        val indicesTestSubscriber = TestSubscriber<List<Int>>()

        // Execute
        hymnDao.getIndicesByNumber().subscribe(indicesTestSubscriber)

        indicesTestSubscriber.assertSubscribed()
        indicesTestSubscriber.assertValue(expectedIndices)
        indicesTestSubscriber.dispose()
    }

    @Test
    @Throws(Exception::class)
    fun get_hymn_indices_sorted_according_to_title() {
        // Setup
        val expectedIndices = hymnList.sortedBy { it.title }.map { it.num }

        val indicesTestSubscriber = TestSubscriber<List<Int>>()

        // Execute
        hymnDao.getIndicesByTitle().subscribe(indicesTestSubscriber)

        indicesTestSubscriber.assertSubscribed()
        indicesTestSubscriber.assertValue(expectedIndices)
        indicesTestSubscriber.dispose()
    }

    @Ignore
    /* I've not figured out how to test the fts search table using in memory database as we use it here*/
    @Test
    @Throws(Exception::class)
    fun searchHymns_returns_correctly_matched_hymns() {
        // Setup
        val searchQuery = "glo"

        val searchResultSubscriber = TestSubscriber<List<SearchResult>>()
        val expected = hymnList.filter {
            it.title.contains(searchQuery, true)
                    || it.verses[0].contains(searchQuery, true)
                    || it.chorus?.contains(searchQuery, true) ?: false
        }.map { SearchResult(it.num, it.title, it.verses, it.chorus) }

        // Execute
        hymnDao.searchHymns(searchQuery).subscribe(searchResultSubscriber)

        searchResultSubscriber.assertSubscribed()
        searchResultSubscriber.assertValue(expected)
        searchResultSubscriber.dispose()
    }
}