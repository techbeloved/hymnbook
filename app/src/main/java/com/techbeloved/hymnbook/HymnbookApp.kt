package com.techbeloved.hymnbook

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import com.google.firebase.FirebaseApp
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.data.model.NightMode
import com.techbeloved.hymnbook.shared.di.AndroidInjector
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import androidx.work.Configuration as WorkerConfig

@HiltAndroidApp
class HymnbookApp : Application(), WorkerConfig.Provider {
    @Inject
    lateinit var sharedPreferencesRepo: SharedPreferencesRepo

    companion object {
        lateinit var instance: HymnbookApp
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

//        setupNightMode()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        FirebaseApp.initializeApp(this)
        AndroidInjector.init(this)
    }

    private val disposables = CompositeDisposable()
    private fun setupNightMode() {
        sharedPreferencesRepo.nightModeActive()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { selectedMode: NightMode ->
                    // If already in night mode, do nothing, and otherwise
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_NO -> when (selectedMode) {
                            NightMode.On -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            NightMode.Off -> {
                                // No need
                            }
                            NightMode.System -> AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                            )
                        }
                        Configuration.UI_MODE_NIGHT_YES -> when (selectedMode) {
                            NightMode.On -> {
                                // No need
                            }
                            NightMode.Off -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            NightMode.System -> AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                            )
                        }

                        Configuration.UI_MODE_NIGHT_UNDEFINED -> when (selectedMode) {
                            NightMode.On -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            NightMode.Off -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            NightMode.System -> AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                            )
                        }
                    }
                },
                { throwable -> Timber.e(throwable, "Failed to toggle night mode") })
            .run { disposables.add(this) }

    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: androidx.work.Configuration
        get() = WorkerConfig.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}