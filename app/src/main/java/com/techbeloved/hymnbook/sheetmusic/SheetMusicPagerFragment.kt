package com.techbeloved.hymnbook.sheetmusic

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentDetailPagerBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymndetail.GestureListener
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.DepthPageTransformer
import timber.log.Timber

class SheetMusicPagerFragment : Fragment() {

    private lateinit var binding: FragmentDetailPagerBinding
    private lateinit var viewModel: SheetMusicPagerViewModel
    private lateinit var detailPagerAdapter: DetailPagerAdapter
    private lateinit var quickSettingsSheet: BottomSheetBehavior<CardView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore current index
        if (savedInstanceState != null) {
            currentItemIndex = savedInstanceState.getInt(CURRENT_ITEM_ID, 1)
        } else {
            val args = arguments?.let { SheetMusicPagerFragmentArgs.fromBundle(it) }
            currentItemIndex = args?.hymnId ?: 1
        }

        val factory: ViewModelProvider.Factory = SheetMusicPagerViewModel.Factory(Injection.provideOnlineRepo().value)
        viewModel = ViewModelProviders.of(this, factory)[SheetMusicPagerViewModel::class.java]
        viewModel.hymnIndicesLive.observe(this, Observer {
            when (it) {
                is Lce.Loading -> showProgressLoading(it.loading)
                is Lce.Content -> initializeViewPager(it.content, currentItemIndex)
                is Lce.Error -> showContentError(it.error)
            }
        })
        viewModel.loadIndices()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_pager, container, false)

        binding.lifecycleOwner = this

        setupImmersiveMode() // Immersive mode

        NavigationUI.setupWithNavController(binding.toolbarDetail, findNavController())
        binding.toolbarDetail.inflateMenu(R.menu.detail)
        binding.toolbarDetail.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_detail_quick_settings -> {
                    showQuickSettingsBottomSheet()
                    true
                }
                else -> false

            }
        }

        quickSettingsSheet = BottomSheetBehavior.from(
                binding.bottomsheetQuickSettings.cardviewQuickSettings)
        setupQuickSettings()


        detailPagerAdapter = DetailPagerAdapter(childFragmentManager)
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CURRENT_ITEM_ID, currentItemIndex)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        showStatusBar()
    }

    private fun showContentError(error: String) {
        // TODO: Implement error screen and show it here
    }

    private fun initializeViewPager(hymnIndices: List<Int>, initialIndex: Int) {
        Timber.i("Initializing viewPager with index: $initialIndex")
        showProgressLoading(false)
        detailPagerAdapter.submitList(hymnIndices)
        // initialIndex represents the hymn number, where as the adapter uses a zero based index
        // Which implies that when the indices is sorted by titles, the correct detail won't be shown.
        // So we just need to find the index from the list of hymn indices

        val indexToLoad = hymnIndices.indexOf(initialIndex)
        binding.viewpagerHymnDetail.currentItem = indexToLoad
        updateToolbarWithCurrentItem(initialIndex)
    }

    private fun showProgressLoading(loading: Boolean) {
        //if (loading) binding.progressBarHymnDetailLoading.visibility = View.VISIBLE
        //else binding.progressBarHymnDetailLoading.visibility = View.GONE
    }

    /**
     * Configure the quick settings found in the bottomsheet
     */
    private fun setupQuickSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)

        // We don't need this font size button here in sheet music
        binding.bottomsheetQuickSettings.buttonQuickSettingsFontIncrease.isEnabled = false
        binding.bottomsheetQuickSettings.buttonQuickSettingsFontDecrease.isEnabled = false

        // night Mode
        val nightModePreference: Preference<Boolean> = rxPreferences.getBoolean(getString(R.string.pref_key_enable_night_mode), false)
        val darkModeEnabled = nightModePreference.get()
        if (darkModeEnabled) {
            binding.bottomsheetQuickSettings.radiobuttonQuickSettingsDarkTheme.isChecked = true
        } else {
            binding.bottomsheetQuickSettings.radiobuttonQuickSettingsLightTheme.isChecked = true
        }
        binding.bottomsheetQuickSettings.radiogroupQuickSettingsThemeSelector.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radiobutton_quick_settings_dark_theme) {
                nightModePreference.set(true)
            } else if (checkedId == R.id.radiobutton_quick_settings_light_theme) {
                nightModePreference.set(false)
            }
        }
    }


    inner class DetailPagerAdapter(private val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val hymnIndices = mutableListOf<Int>()
        override fun getItem(position: Int): Fragment {
            val detailFragment = SheetMusicDetailFragment()
            return if (position < hymnIndices.size) {
                detailFragment.init(hymnIndices[position])
                detailFragment
            } else {
                detailFragment.init(1)
                detailFragment
            }
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            updateToolbarWithCurrentItem(hymnIndices[position])
        }

        override fun getCount(): Int {
            return hymnIndices.size
        }

        fun submitList(hymnIndices: List<Int>) {
            this.hymnIndices.clear()
            this.hymnIndices.addAll(hymnIndices)
            notifyDataSetChanged()
        }

    }

    private var currentItemIndex = -1
    private fun updateToolbarWithCurrentItem(hymnIndex: Int) {
        currentItemIndex = hymnIndex
        binding.toolbarDetail.title = "Hymn, $currentItemIndex"
    }

    private fun setupImmersiveMode() {
        // Setup the gesture listener to listen for single tap and toggle fullscreen
        val mainView = binding.touchableFrameHymnDetail
        val gestureListener = GestureListener {
            toggleHideyBar()
            false
        }
        val gd = GestureDetector(activity, gestureListener)
        mainView.setGestureDetector(gd)
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    private fun toggleHideyBar() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        var newUiOptions = activity!!.window.decorView.systemUiVisibility
        // END_INCLUDE (get_current_ui_flags)
        // Hide or show toolbar accordingly
        val isImmersiveModeEnabled = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == newUiOptions
        if (isImmersiveModeEnabled) {
            binding.toolbarDetail.visibility = View.VISIBLE
        } else {
            binding.toolbarDetail.visibility = View.GONE
        }

        // BEGIN_INCLUDE (toggle_ui_flags)
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN


        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        activity!!.window.decorView.systemUiVisibility = newUiOptions
        //END_INCLUDE (set_ui_flags)
    }

    private fun showStatusBar() {
        var newUiOptions = activity!!.window.decorView.systemUiVisibility
        val isImmersiveModeEnabled = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == newUiOptions
        if (isImmersiveModeEnabled) {

            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN

            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            activity!!.window.decorView.systemUiVisibility = newUiOptions
        }
    }

    private fun showQuickSettingsBottomSheet() {
        //val quickSettingsFragment = QuickSettingsFragment()
        //fragmentManager?.let { quickSettingsFragment.show(it, quickSettingsFragment.tag) }
        if (quickSettingsSheet.state != BottomSheetBehavior.STATE_EXPANDED) {
            quickSettingsSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}

private const val CURRENT_ITEM_ID = "currentItemId"