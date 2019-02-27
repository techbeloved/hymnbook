package com.techbeloved.hymnbook

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.data.repo.local.util.AppExecutors
import com.techbeloved.hymnbook.data.repo.local.util.DataGenerator
import timber.log.Timber

class HymnbookApp : Application() {
    companion object {
        lateinit var database: HymnsDatabase
        lateinit var executors: AppExecutors

        lateinit var instance: HymnbookApp
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        executors = AppExecutors()

        // DONE: Enable when ready for proper implementation
        buildDatabase(this, executors)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    private fun buildDatabase(context: Context, executors: AppExecutors) {
        HymnbookApp.database = Room.databaseBuilder(context,
                HymnsDatabase::class.java, "hymns.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        executors.diskIO().execute {
                            val hymns: List<Hymn> = DataGenerator.generateHymns()
                            val topics = DataGenerator.generateTopics()
                            insertInitialData(HymnbookApp.database, hymns, topics)
                        }
                    }
                })
                .build()
    }

    private fun insertInitialData(hymnDatabase: HymnsDatabase, hymns: List<Hymn>, topics: List<Topic>) {
        hymnDatabase.runInTransaction {
            hymnDatabase.topicDao().insertAll(topics)
            hymnDatabase.hymnDao().insertAll(hymns)

            Timber.i("Successfully inserted ${hymns.size} hymns\nand ${topics.size} topics, into the database")
        }
    }
}