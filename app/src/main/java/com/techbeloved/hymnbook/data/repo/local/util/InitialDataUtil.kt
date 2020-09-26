package com.techbeloved.hymnbook.data.repo.local.util

import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class InitialDataUtil @Inject constructor(private val database: Provider<HymnsDatabase>) {
    fun insertInitialData(hymns: List<Hymn>, topics: List<Topic>) {
        database.get().run {
            runInTransaction {
                topicDao().insertAll(topics)
                hymnDao().insertAll(hymns)
                Timber.i("Successfully inserted ${hymns.size} hymns\nand ${topics.size} topics, into the database")
            }
        }
    }
}