package com.techbeloved.hymnbook

import android.app.DownloadManager
import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.databinding.ActivityHymnbookBinding
import com.techbeloved.hymnbook.services.FileManagerService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

class HymnbookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHymnbookBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hymnbook)
        setupNightMode()

        navController = findNavController(R.id.mainNavHostFragment)

        binding.bottomNavigationMain.setupWithNavController(navController)
        binding.bottomNavigationMain.setOnNavigationItemSelectedListener { item ->
            if (navController.currentDestination?.id != item.itemId) {
                item.onNavDestinationSelected(navController)
                true
            } else false
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            // Hide the bottom navigation in detail view
            if (destination.id == R.id.detailPagerFragment) {
                if (binding.bottomNavigationMain.isVisible) binding.bottomNavigationMain.visibility = View.INVISIBLE
            } else {
                if (!binding.bottomNavigationMain.isVisible) binding.bottomNavigationMain.visibility = View.VISIBLE
            }
        }

        setupSharePreferences()

        checkIfAnyDownloadIsOngoing()
    }

    private fun setupSharePreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        rxPreferences = RxSharedPreferences.create(sharedPreferences)
        currentDownloadIdsPref = rxPreferences.getStringSet(getString(R.string.pref_key_current_download_id))

        // Attempt to unzip any file that might not have been unzipped
        val catalogReadyPref = rxPreferences.getBoolean(getString(R.string.pref_key_hymn_catalog_ready), false)
        catalogReadyPref.asObservable().subscribeOn(Schedulers.io())
                .doOnNext { ready ->
                    if (!ready) {
                        unzipFiles(10)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ ready ->
                    if (!ready) {
                        Timber.i("Files not ready")
                    }
                }, { Timber.w(it) })
                .run { disposables.add(this) }

    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rxPreferences: RxSharedPreferences
    private lateinit var currentDownloadIdsPref: Preference<Set<String>>
    private fun checkIfAnyDownloadIsOngoing() {
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        currentDownloadIdsPref.asObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ ids ->
                    downloadIds = ids
                }, { Timber.w(it) })
                .run { disposables.add(this) }
    }


    private var onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.i("Received download complete intent")
            val id: Long? = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            Timber.i("DownloadId: %s", id)
            id?.let {
                if (it >= 0) unzipFiles(it)
            }
        }
    }

    private var downloadIds: Set<String> = emptySet()
    private fun unzipFiles(completedDownloadId: Long) {
        if (completedDownloadId.toString() in downloadIds) {
            val updatedDownloadIds = downloadIds.minus(completedDownloadId.toString())
            currentDownloadIdsPref.set(updatedDownloadIds)
        }
        val downloads = File(getExternalFilesDir(null), getString(R.string.file_path_downloads))
        if (downloads.exists()) {
            for (file in downloads.listFiles()) {
                Timber.i("File is zip: %s", file.endsWith(".zip"))
                if (file.isFile && file.extension == "zip") {
                    Timber.i("Got a zip file: %s", file.name)
                    val destination = when {
                        file.name.contains("catalog") -> File(getExternalFilesDir(null), getString(R.string.file_path_catalogs)).absolutePath
                        file.name.contains("midi") -> File(getExternalFilesDir(null), getString(R.string.file_path_midi)).absolutePath
                        else -> File(getExternalFilesDir(null), "others").absolutePath
                    }
                    FileManagerService.startActionUnzipFile(applicationContext, "$destination/", file.absolutePath)
                    Timber.i("destination: %s", destination)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (navController.currentDestination?.id != item.itemId && item.onNavDestinationSelected(navController))
                || super.onOptionsItemSelected(item)
    }

    private val disposables: CompositeDisposable = CompositeDisposable()
    private fun setupNightMode() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)

        val nightModePreference: Preference<Boolean> = rxPreferences.getBoolean(getString(R.string.pref_key_enable_night_mode), false)
        val disposable = nightModePreference.asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { enable ->

                    // If already in night mode, do nothing, and otherwise
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_NO -> {
                            if (enable) delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                        }
                        Configuration.UI_MODE_NIGHT_YES -> {
                            if (!enable) delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                            if (!enable) delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                    }
                }
        disposables.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
        unregisterReceiver(onDownloadComplete)
    }
}
