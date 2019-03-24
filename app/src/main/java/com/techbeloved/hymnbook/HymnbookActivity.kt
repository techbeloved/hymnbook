package com.techbeloved.hymnbook

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.get
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.databinding.ActivityHymnbookBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HymnbookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHymnbookBinding
    private lateinit var navController: NavController

    private lateinit var viewModel: HymnbookViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hymnbook)
        setupNightMode()

        navController = findNavController(R.id.mainNavHostFragment)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.bottomNavigationMain.setupWithNavController(navController)
        //binding.toolbarMain.setupWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            // Hide the bottom navigation in detail view
            if (destination.id == R.id.detailPagerFragment) {
                if (binding.bottomNavigationMain.isVisible) binding.bottomNavigationMain.visibility = View.INVISIBLE
            } else {
                if (!binding.bottomNavigationMain.isVisible) binding.bottomNavigationMain.visibility = View.VISIBLE
            }
        }

        // setup the shared viewmodel
        viewModel = ViewModelProviders.of(this).get(HymnbookViewModel::class.java)
        //viewModel.toolbarTitle.observe(this, Observer { updateToolbarTitle(it) })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun updateToolbarTitle(title: String) {
        //binding.toolbarMain.title = title
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

                    val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    // If already in night mode, do nothing, and otherwise
                    when (currentNightMode) {
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
}
