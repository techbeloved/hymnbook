package com.techbeloved.hymnbook.di

import android.app.Application
import android.content.ComponentName
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.techbeloved.hymnbook.HymnbookApp
import com.techbeloved.hymnbook.HymnbookUseCases
import com.techbeloved.hymnbook.HymnbookUseCasesImp
import com.techbeloved.hymnbook.MediaSessionConnection
import com.techbeloved.hymnbook.data.FileManager
import com.techbeloved.hymnbook.data.FileManagerImp
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.data.SharedPreferencesRepoImp
import com.techbeloved.hymnbook.data.download.Downloader
import com.techbeloved.hymnbook.data.download.DownloaderImp
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.FirebaseRepo
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.HymnsRepositoryImp
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.data.repo.local.util.AppExecutors
import com.techbeloved.hymnbook.data.repo.local.util.DataGenerator
import com.techbeloved.hymnbook.sheetmusic.HymnUseCases
import com.techbeloved.hymnbook.sheetmusic.HymnsUseCasesImp
import com.techbeloved.hymnbook.tunesplayback.TunesPlayerService
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.Executors

const val WCCRM_HYMNS_COLLECTION = "wccrm"

object Injection {
    fun provideAppContext(): Application {
        return HymnbookApp.instance
    }

    val executors by lazy {
        AppExecutors()
    }

    /**
     * Lazily build the database as well as download midi archive when done
     */
    private val database: HymnsDatabase by lazy {
        Room.databaseBuilder(provideAppContext(),
                HymnsDatabase::class.java, "hymns.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        executors.diskIO().execute {
                            val hymns: List<Hymn> = DataGenerator.generateHymns()
                            val topics = DataGenerator.generateTopics()
                            insertInitialData(hymns, topics)
                            // Schedule download of midi archive here
                            provideHymnbookUseCases.downloadLatestHymnMidiArchive()
                        }
                    }
                })
                .build()
    }

    val provideFileManager: FileManager by lazy { FileManagerImp(provideAppContext(), provideSharePrefsRepo) }

    val provideRepository: HymnsRepository by lazy {
        HymnsRepositoryImp.getInstance(database)
    }

    val provideOnlineRepo: OnlineRepo by lazy {
        FirebaseRepo(Executors.newSingleThreadExecutor(), FirebaseFirestore.getInstance(), WCCRM_HYMNS_COLLECTION)
    }

    val provideSharePrefsRepo: SharedPreferencesRepo by lazy {
        SharedPreferencesRepoImp(
                RxSharedPreferences.create(PreferenceManager
                        .getDefaultSharedPreferences(HymnbookApp.instance)),
                HymnbookApp.instance.resources
        )
    }

    /**
     *
     */
    val provideHymnListingUseCases: HymnUseCases by lazy {
        HymnsUseCasesImp(provideRepository,
                provideOnlineRepo,
                provideSchedulers,
                provideDownloader,
                provideSharePrefsRepo)
    }

    /**
     * Simple downloader
     */
    val provideDownloader: Downloader by lazy {
        val cacheDir = HymnbookApp.instance.getExternalFilesDir(null)
                ?: HymnbookApp.instance.filesDir
        DownloaderImp(FirebaseStorage.getInstance(),
                cacheDir,
                provideRepository,
                provideOnlineRepo,
                provideSchedulers,
                Executors.newSingleThreadExecutor())
    }

    /**
     * Hymnbook app wide use cases.
     */
    val provideHymnbookUseCases: HymnbookUseCases by lazy {
        HymnbookUseCasesImp(
                HymnbookApp.instance,
                provideSharePrefsRepo,
                WorkManager.getInstance(HymnbookApp.instance)
        )
    }

    /**
     * Schedulers used by RxJava stuff
     */
    val provideSchedulers: SchedulerProvider by lazy {
        object : SchedulerProvider {
            override fun io(): Scheduler = Schedulers.io()

            override fun ui(): Scheduler = AndroidSchedulers.mainThread()
        }
    }

    val provideMediaSessionConnection: MediaSessionConnection by lazy {
        MediaSessionConnection.getInstance(provideAppContext(),
                ComponentName(provideAppContext(), TunesPlayerService::class.java))
    }


    /**
     * Insert locally provided initial data for our hymnbook
     */
    private fun insertInitialData(hymns: List<Hymn>, topics: List<Topic>) {
        database.runInTransaction {
            database.topicDao().insertAll(topics)
            database.hymnDao().insertAll(hymns)

            Timber.i("Successfully inserted ${hymns.size} hymns\nand ${topics.size} topics, into the database")
        }
    }
}