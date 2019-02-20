package com.techbeloved.hymnbook.data.repo.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.techbeloved.hymnbook.data.model.Topic
import io.reactivex.Flowable

@Dao
interface TopicDao {

    @Query("SELECT * FROM topics ORDER BY id ASC")
    fun getAllTopics(): Flowable<List<Topic>>

    @Query("SELECT * FROM topics WHERE id = :id")
    fun getTopicById(id: Int): Flowable<Topic>

    @Insert
    fun insertTopic(topic: Topic)

    @Insert
    fun insertAll(topics: List<Topic>)

    @Delete
    fun delete(topic: Topic)

    @Query("DELETE FROM topics WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM topics")
    fun deleteAll()

}