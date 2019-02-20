package com.techbeloved.hymnbook.data.repo.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.local.util.AppExecutors


@Database(entities = [Hymn::class, Topic::class], views = [HymnTitle::class, HymnDetail::class], version = 2)
@TypeConverters(ListConverter::class)
abstract class HymnsDatabase: RoomDatabase() {
    abstract fun hymnDao(): HymnDao
    abstract fun topicDao(): TopicDao
}
