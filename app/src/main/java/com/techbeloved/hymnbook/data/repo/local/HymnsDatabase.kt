package com.techbeloved.hymnbook.data.repo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techbeloved.hymnbook.data.model.*


@Database(entities = [Hymn::class, Topic::class, HymnSearch::class, Favorite::class, Playlist::class],
        views = [HymnTitle::class, HymnDetail::class],
        version = 5)
@TypeConverters(ListConverter::class, DateConverter::class)
abstract class HymnsDatabase : RoomDatabase() {
    abstract fun hymnDao(): HymnDao
    abstract fun topicDao(): TopicDao
    abstract fun playlistsDao(): PlaylistsDao
}
