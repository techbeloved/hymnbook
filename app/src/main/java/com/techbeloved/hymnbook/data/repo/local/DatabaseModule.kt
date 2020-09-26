package com.techbeloved.hymnbook.data.repo.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.repo.local.util.DataGenerator
import com.techbeloved.hymnbook.data.repo.local.util.InitialDataUtil
import com.techbeloved.hymnbook.usecases.HymnbookUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHymnDatabase(@ApplicationContext context: Context,
                            initialDataUtil: InitialDataUtil,
                            hymnbookUseCases: Provider<HymnbookUseCases>,
                            @Named("IO") executor: Executor): HymnsDatabase {
        return Room.databaseBuilder(context,
                HymnsDatabase::class.java, "hymns.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        executor.execute {
                            val hymns: List<Hymn> = DataGenerator.generateHymns()
                            val topics = DataGenerator.generateTopics()
                            initialDataUtil.insertInitialData(hymns, topics)
                            // Schedule download of midi archive here
                            hymnbookUseCases.get().downloadLatestHymnMidiArchive()
                        }
                    }
                })
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