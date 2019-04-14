package com.techbeloved.hymnbook

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.data.repo.local.util.AppExecutors
import com.techbeloved.hymnbook.data.repo.local.util.DataGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

        setupNightMode()

        // DONE: Enable when ready for proper implementation
        buildDatabase(this, executors)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

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

    private val disposables = CompositeDisposable()
    private fun setupNightMode() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)

        val nightModePreference: Preference<Boolean> = rxPreferences.getBoolean(getString(R.string.pref_key_enable_night_mode), false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            nightModePreference.asObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { enabled ->
                                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                                // If already in night mode, do nothing, and otherwise
                                when (currentNightMode) {
                                    Configuration.UI_MODE_NIGHT_NO -> {
                                        if (enabled) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                                    }
                                    Configuration.UI_MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                                        if (!enabled) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                                    }
                                }
                            },
                            { throwable -> Timber.e(throwable, "Failed to toggle night mode") })
                    .run { disposables.add(this) }
        }

    }
}