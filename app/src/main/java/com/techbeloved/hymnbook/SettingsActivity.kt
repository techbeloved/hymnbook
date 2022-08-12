package com.techbeloved.hymnbook

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.techbeloved.hymnbook.data.model.NightMode
import com.techbeloved.hymnbook.databinding.ActivitySettingsMainBinding
import com.techbeloved.hymnbook.hymndetail.QuickSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val quickSettingsViewModel: QuickSettingsViewModel by viewModels()
    private lateinit var binding: ActivitySettingsMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_main)
        setupNightMode()

        navController = findNavController(R.id.settingsNavHostFragment)

        binding.toolbarSettingsMain.setupWithNavController(navController)

        setSupportActionBar(binding.toolbarSettingsMain)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
}
