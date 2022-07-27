package com.techbeloved.hymnbook

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.techbeloved.hymnbook.databinding.ActivityHymnbookBinding
import com.techbeloved.hymnbook.home.HomeFragmentDirections
import com.techbeloved.hymnbook.utils.CATEGORY_WCCRM_SHEET_MUSIC
import com.techbeloved.hymnbook.utils.category
import com.techbeloved.hymnbook.utils.hymnId
import com.techbeloved.hymnbook.utils.isValidHymnUri
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

@AndroidEntryPoint
class HymnbookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHymnbookBinding
    private lateinit var navController: NavController

    private val viewModel: HymnbookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hymnbook)
        setupNightMode()

        navController = findNavController(R.id.mainNavHostFragment)

        binding.bottomNavigationMain.setupWithNavController(navController)
        binding.bottomNavigationMain.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id != item.itemId) {
                item.onNavDestinationSelected(navController)
                true
            } else false
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Hide the bottom navigation in detail view
            when (destination.id) {
                R.id.detailPagerFragment,
                R.id.sheetMusicPagerFragment -> if (binding.bottomNavigationMain.isVisible) {
                    binding.bottomNavigationMain.visibility = View.INVISIBLE
                }
                else -> if (!binding.bottomNavigationMain.isVisible) binding.bottomNavigationMain.visibility = View.VISIBLE
            }
        }

        FirebaseAnalytics.getInstance(this.applicationContext)
        handleDynamicLinks(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
                            if (enable) delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                            else delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

                        }
                        Configuration.UI_MODE_NIGHT_YES -> {
                            if (!enable) delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        }
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                            if (!enable) delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        }
                    }
                }
        disposables.add(disposable)
    }

    private fun handleDynamicLinks(intent: Intent) {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                    }

                    Timber.i("Deep link received: %s", deepLink)

                    deepLink?.let { uri ->
                        val uriString = uri.toString()
                        if (uriString.category() == CATEGORY_WCCRM_SHEET_MUSIC && uriString.isValidHymnUri()) {
                            navController.navigate(HomeFragmentDirections
                                    .actionHomeFragmentToSheetMusicPagerFragment(uriString.hymnId()?.toInt()
                                            ?: 1))
                        } else {
                            navController.navigate(HomeFragmentDirections.actionHomeFragmentToDetailPagerFragment(uriString))
                        }
                    }

                }
                .addOnFailureListener(this) { e -> Timber.w(e, "getDynamicLink:onFailure") }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
        viewModel.updateAppFirstStart(false)
        //unregisterReceiver(onDownloadComplete)
    }
}
