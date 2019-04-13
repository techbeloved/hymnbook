package com.techbeloved.hymnbook.hymndetail


import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.techbeloved.hymnbook.HymnbookViewModel

import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentDetailPagerBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.DepthPageTransformer
import com.techbeloved.hymnbook.viewgroup.TouchableFrameWrapper
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 *
 */
class DetailPagerFragment : Fragment() {

    private lateinit var detailPagerAdapter: DetailPagerAdapter

    private lateinit var viewModel: HymnPagerViewModel
    private lateinit var binding: FragmentDetailPagerBinding
    private lateinit var quickSettingsSheet: BottomSheetBehavior<ConstraintLayout>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
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
                binding.bottomsheetQuickSettings.constraintlayoutQuickSettings)
        setupQuickSettings()


        detailPagerAdapter = DetailPagerAdapter(childFragmentManager)
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())

        val args = arguments?.let { DetailPagerFragmentArgs.fromBundle(it) }
        initialIndex = args?.hymnId ?: 1
        return binding.root
    }

    /**
     * Configure the quick settings found in the bottomsheet
     */
    private fun setupQuickSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)
        val defaultTextSize = resources.getInteger(R.integer.normal_detail_text_size).toFloat()
        val fontSizePreference: Preference<Float> = rxPreferences.getFloat(
                getString(R.string.pref_key_detail_font_size), defaultTextSize)

        // Example, max_text_size = 6, maxIncrement = 6/20 = 0.3. So we cannot add more than 0.3 to original text size
        val maxTextIncrement = resources.getInteger(R.integer.max_text_size)
        val minTextIncrement = resources.getInteger(R.integer.min_text_size)
        Timber.i("minTextSize: %s", minTextIncrement)

        binding.bottomsheetQuickSettings.buttonQuickSettingsFontIncrease.setOnClickListener { v ->
            val currentSize = fontSizePreference.get()
            if (currentSize - defaultTextSize < maxTextIncrement) {
                Timber.i("Current: %s", currentSize)
                fontSizePreference.set(currentSize + 1f)
            }
        }

        binding.bottomsheetQuickSettings.buttonQuickSettingsFontDecrease.setOnClickListener { v ->
            val currentSize = fontSizePreference.get()
            if (currentSize - defaultTextSize > minTextIncrement) {
                fontSizePreference.set(currentSize - 1f)
                Timber.i("Current: %s", currentSize)
            }
        }

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

    private var initialIndex: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = HymnPagerViewModel.Factory(Injection.provideRepository())
        viewModel = ViewModelProviders.of(this, factory).get(HymnPagerViewModel::class.java)
        Timber.i("onActivityCreated")
        viewModel.hymnIndicesLiveData.observe(this, Observer {
            when (it) {
                is Lce.Loading -> showProgressLoading(it.loading)
                is Lce.Content -> initializeViewPager(it.content, initialIndex)
                is Lce.Error -> showContentError(it.error)
            }
        })

        viewModel.loadHymnIndices()
    }

    private lateinit var mainViewModel: HymnbookViewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = ViewModelProviders.of(activity!!).get(HymnbookViewModel::class.java)
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
    }

    private fun showProgressLoading(loading: Boolean) {
        //if (loading) binding.progressBarHymnDetailLoading.visibility = View.VISIBLE
        //else binding.progressBarHymnDetailLoading.visibility = View.GONE
    }

    inner class DetailPagerAdapter(private val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val hymnIndices = mutableListOf<Int>()
        override fun getItem(position: Int): Fragment {
            val detailFragment = DetailFragment()
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
        if (hymnIndex == currentItemIndex) return
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
