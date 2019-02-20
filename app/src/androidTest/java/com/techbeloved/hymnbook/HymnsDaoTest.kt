package com.techbeloved.hymnbook

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.data.repo.local.TopicDao
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

    val hymn1 = Hymn("hymn_1", 1, "one", listOf("verse1", "verse2"))
    val hymn2 = Hymn("hymn_2", 2, "two", listOf("verse1", "verse2"))
    val hymn3 = Hymn("hymn_3", 3, "three", listOf("verse1", "verse2"))

    private val topic1 = Topic(1, "topic1")
    private val topic2 = Topic(2, "topic2")
    private val topic3 = Topic(3, "topic3")

    private val detail1 = HymnDetail("hymn_1", 1, "one", listOf("verse1", "verse2"), "topic1")
    private val detail2 = HymnDetail("hymn_2", 2, "two", listOf("verse1", "verse2"), "topic1")
    private val detail3 = HymnDetail("hymn_3", 3, "three", listOf("verse1", "verse2"), "topic3")

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

        val expected = listOf(HymnTitle(1, "one"),
                HymnTitle(2, "two"), HymnTitle(3, "three"))
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

        detailFlow.subscribe(testHymnDetailSubscriber)

        testHymnDetailSubscriber.assertSubscribed()
        testHymnDetailSubscriber.assertValue(detail2)
        testHymnDetailSubscriber.dispose()
    }
}