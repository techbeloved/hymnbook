package com.techbeloved.hymnbook

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.data.repo.local.util.AppExecutors
import com.techbeloved.hymnbook.data.repo.local.util.DataGenerator

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

        buildDatabase(this, executors)

    }

    private fun buildDatabase(context: Context, executors: AppExecutors) {
        HymnbookApp.database = Room.databaseBuilder(context,
                HymnsDatabase::class.java, "hymns.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        executors.diskIO().execute {
                            val hymns: List<Hymn> = DataGenerator.generateHymns()
                            insertInitialData(HymnbookApp.database, hymns)
                        }
                    }
                })
                .build()
    }

    private fun insertInitialData(hymnDatabase: HymnsDatabase, hymns: List<Hymn>) {
        hymnDatabase.runInTransaction {
            hymnDatabase.hymnDao().insertAll(hymns)
        }
    }
}