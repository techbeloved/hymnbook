package com.techbeloved.hymnbook.usecases

import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.local.TopicDao
import javax.inject.Inject

class InsertTopicsUseCase @Inject constructor(private val topicDao: TopicDao) {

    operator fun invoke(topics: List<Topic>) = topicDao.insertAll(topics)
}
