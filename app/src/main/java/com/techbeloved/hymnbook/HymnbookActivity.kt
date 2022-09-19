package com.techbeloved.hymnbook

import android.content.Intent
import android.content.res.Configuration
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.techbeloved.hymnbook.data.model.NightMode
import com.techbeloved.hymnbook.databinding.ActivityHymnbookBinding
import com.techbeloved.hymnbook.hymndetail.QuickSettingsViewModel
import com.techbeloved.hymnbook.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HymnbookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHymnbookBinding
    private lateinit var navController: NavController

    private val viewModel: HymnbookViewModel by viewModels()
    private val quickSettingsViewModel: QuickSettingsViewModel by viewModels()

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
                R.id.detailPagerFragment -> if (binding.bottomNavigationMain.isVisible) {
                    binding.bottomNavigationMain.visibility = View.INVISIBLE
                }
                else -> if (!binding.bottomNavigationMain.isVisible) binding.bottomNavigationMain.visibility =
                    View.VISIBLE
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
        return (navController.currentDestination?.id != item.itemId && item.onNavDestinationSelected(
            navController
        )) || super.onOptionsItemSelected(item)
    }

    private fun setupNightMode() {

        quickSettingsViewModel.nightMode.observe(this) { selectedMode: NightMode ->
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> when (selectedMode) {
                    NightMode.On -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                    NightMode.Off -> {
                        // No need
                    }
                    NightMode.System -> delegate.localNightMode =
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }

                Configuration.UI_MODE_NIGHT_YES -> when (selectedMode) {
                    NightMode.On -> {
                        // No need
                    }
                    NightMode.Off -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                    NightMode.System -> delegate.localNightMode =
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

                }

                Configuration.UI_MODE_NIGHT_UNDEFINED -> when (selectedMode) {
                    NightMode.On -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                    NightMode.Off -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                    NightMode.System -> delegate.localNightMode =
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

                }
            }
        }
    }

    private fun handleDynamicLinks(intent: Intent) {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                val deepLink = pendingDynamicLinkData?.link
                if (deepLink != null) {
                    navController.safeNavigate(deepLink)
                }

            }
            .addOnFailureListener(this) { e -> Timber.w(e, "getDynamicLink:onFailure") }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateAppFirstStart(false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onShown()
    }
}
