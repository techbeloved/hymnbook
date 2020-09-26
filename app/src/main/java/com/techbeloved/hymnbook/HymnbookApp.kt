package com.techbeloved.hymnbook

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import androidx.work.Configuration as WorkerConfig

@HiltAndroidApp
class HymnbookApp : Application(), WorkerConfig.Provider {
    companion object {
        lateinit var instance: HymnbookApp
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        setupNightMode()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        FirebaseApp.initializeApp(this)
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

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration(): WorkerConfig = WorkerConfig.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}