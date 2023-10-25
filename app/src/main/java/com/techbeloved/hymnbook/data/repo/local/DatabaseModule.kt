package com.techbeloved.hymnbook.data.repo.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHymnDatabase(
        @ApplicationContext context: Context,
    ): HymnsDatabase {
        return Room.databaseBuilder(
            context,
            HymnsDatabase::class.java, "hymns.db"
        )
            .fallbackToDestructiveMigrationFrom(1,2,3,4,5)
            .build()
    }

    @Provides
    fun providesPlaylistDao(database: HymnsDatabase) = database.playlistsDao()

    @Provides
    fun provideHymnDao(database: HymnsDatabase) = database.hymnDao()

    @Provides
    fun provides(database: HymnsDatabase) = database.topicDao()

    @Provides
    @Singleton
    @Named("IO")
    fun provideDiskIOExecutor(): Executor = Executors.newSingleThreadExecutor()

}