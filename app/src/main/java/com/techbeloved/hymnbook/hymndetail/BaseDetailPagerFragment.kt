package com.techbeloved.hymnbook.hymndetail

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.data.model.NightMode
import com.techbeloved.hymnbook.databinding.DialogTempoSelectorBinding
import com.techbeloved.hymnbook.databinding.FragmentDetailPagerBinding
import com.techbeloved.hymnbook.nowplaying.NowPlayingViewModel
import com.techbeloved.hymnbook.nowplaying.PlaybackEvent
import com.techbeloved.hymnbook.playlists.AddToPlaylistDialogFragment
import com.techbeloved.hymnbook.playlists.EXTRA_SELECTED_HYMN_ID
import com.techbeloved.hymnbook.tunesplayback.duration
import timber.log.Timber
import javax.inject.Inject

abstract class BaseDetailPagerFragment : Fragment() {
    @Inject
    lateinit var sharedPreferencesRepo: SharedPreferencesRepo
    private var _currentHymnId: Int = 1
    private val nowPlayingViewModel: NowPlayingViewModel by viewModels()
    private val quickSettingsViewModel: QuickSettingsViewModel by activityViewModels()

    private var _binding: FragmentDetailPagerBinding? = null
    protected val binding: FragmentDetailPagerBinding get() = _binding!!
    private lateinit var quickSettingsSheet: BottomSheetBehavior<CardView>
    private lateinit var playControlsSheet: BottomSheetBehavior<CardView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_pager, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.nowPlaying = nowPlayingViewModel

        NavigationUI.setupWithNavController(binding.toolbarDetail, findNavController())
        binding.toolbarDetail.inflateMenu(R.menu.detail)
        binding.toolbarDetail.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_detail_quick_settings -> {
                    showQuickSettingsBottomSheet()
                    true
                }
                R.id.menu_detail_add_to_playlist -> {
                    val playlistDialog = AddToPlaylistDialogFragment().apply {
                        arguments = Bundle().apply { putInt(EXTRA_SELECTED_HYMN_ID, currentHymnId) }
                    }
                    playlistDialog.show(childFragmentManager, null)
                    true
                }
                R.id.menu_detail_share -> {
                    initiateContentSharing()
                    true
                }
                else -> false

            }
        }

        quickSettingsSheet = BottomSheetBehavior.from(
            binding.bottomsheetQuickSettings.cardviewQuickSettings
        )
        playControlsSheet = BottomSheetBehavior.from(
            binding.bottomsheetPlayControls.cardViewPlayControls
        )
        setupQuickSettings()

        setupMediaPlaybackControls()

        binding.viewpagerHymnDetail.addOnPageChangeListener(pageChangeListener)

        return binding.root
    }

    private var previousState = ViewPager.SCROLL_STATE_IDLE
    private var userScrollChange = false
    private val pageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (previousState == ViewPager.SCROLL_STATE_DRAGGING
                    && state == ViewPager.SCROLL_STATE_SETTLING
                )
                    userScrollChange = true
                else if (previousState == ViewPager.SCROLL_STATE_SETTLING
                    && state == ViewPager.SCROLL_STATE_IDLE
                )
                    userScrollChange = false

                previousState = state
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (userScrollChange) nowPlayingViewModel.skipTo(position)
            }

        }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupMediaPlaybackControls() {
        // Only enable play button when music service is connected
        nowPlayingViewModel.isConnected.observe(viewLifecycleOwner) { connected ->
            binding.bottomsheetPlayControls.cardViewPlayControls.isEnabled = connected
        }
        binding.bottomsheetPlayControls.imageViewControlsPlayPause.setOnClickListener {
            nowPlayingViewModel.playMedia(_currentHymnId.toString())
        }

        binding.bottomsheetPlayControls.imageViewControlsNext.setOnClickListener {
            val currentIndex = binding.viewpagerHymnDetail.currentItem
            val nextIndex = if (currentIndex < nowPlayingViewModel.hymnItems.size - 1) {
                currentIndex + 1
            } else {
                0
            }
            binding.viewpagerHymnDetail.setCurrentItem(nextIndex, true)
            nowPlayingViewModel.skipTo(nextIndex)
        }

        binding.bottomsheetPlayControls.imageViewControlsPrevious.setOnClickListener {
            val currentIndex = binding.viewpagerHymnDetail.currentItem
            val nextIndex = if (currentIndex > 0) {
                currentIndex - 1
            } else {
                nowPlayingViewModel.hymnItems.size - 1
            }
            binding.viewpagerHymnDetail.setCurrentItem(nextIndex, true)
            nowPlayingViewModel.skipTo(nextIndex)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupTempoControls()
        }

        nowPlayingViewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            if (playing) {
                val playToPause =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.avd_play_to_pause
                    ) as AnimatedVectorDrawable
                binding.bottomsheetPlayControls.imageViewControlsPlayPause.setImageDrawable(
                    playToPause
                )
                playToPause.start()
            } else {
                val pauseToPlay =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.avd_pause_to_play
                    ) as AnimatedVectorDrawable
                binding.bottomsheetPlayControls.imageViewControlsPlayPause.setImageDrawable(
                    pauseToPlay
                )
                pauseToPlay.start()
            }
        }

        nowPlayingViewModel.mediaPosition.observe(viewLifecycleOwner) { position ->
            val currentPosition = if (position < 0) 0 else position
            binding.bottomsheetPlayControls.progressbarControlsProgress.progress =
                currentPosition.toFloat()
        }

        nowPlayingViewModel.metadata.observe(viewLifecycleOwner) { metadata ->
            val duration = metadata.duration
            if (duration > 0) {
                binding.bottomsheetPlayControls.progressbarControlsProgress.maximum =
                    duration.toFloat()
            }
        }

        nowPlayingViewModel.repeatMode.observe(viewLifecycleOwner) { mode ->
            Timber.i("Repeat mode changed: %s", mode)
            when (mode) {
                PlaybackStateCompat.REPEAT_MODE_NONE -> {
                    binding.bottomsheetPlayControls.imageViewControlsRepeatToggle.setImageResource(R.drawable.ic_times_one)
                    binding.bottomsheetPlayControls.progressbarControlsProgress.isIndeterminate =
                        false
                }
                PlaybackStateCompat.REPEAT_MODE_ALL -> {
                    binding.bottomsheetPlayControls.imageViewControlsRepeatToggle.setImageResource(R.drawable.ic_times_all)
                    binding.bottomsheetPlayControls.progressbarControlsProgress.isIndeterminate =
                        false
                }
                PlaybackStateCompat.REPEAT_MODE_ONE -> {
                    binding.bottomsheetPlayControls.imageViewControlsRepeatToggle.setImageResource(R.drawable.ic_repeat_active)
                }
            }
        }

        binding.bottomsheetPlayControls.imageViewControlsRepeatToggle.setOnClickListener { nowPlayingViewModel.cycleRepeatMode() }

        nowPlayingViewModel.playbackEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is PlaybackEvent.Error -> Snackbar.make(
                    binding.coordinatorLayoutHymnDetail.rootView,
                    event.message,
                    Snackbar.LENGTH_SHORT
                ).show()
                is PlaybackEvent.Message -> Snackbar.make(
                    binding.coordinatorLayoutHymnDetail,
                    event.message,
                    Snackbar.LENGTH_SHORT
                ).show()
                PlaybackEvent.None -> { /* Do nothing */
                    Timber.i("Nothing received!")
                }
            }
        }

    }

    @RequiresApi(23)
    private fun setupTempoControls() {
        val tempoDialog = BottomSheetDialog(requireActivity())
        val tempoViewBinding: DialogTempoSelectorBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.dialog_tempo_selector, null, false)
        tempoDialog.setContentView(tempoViewBinding.root)

        nowPlayingViewModel.playbackTempo.observe(viewLifecycleOwner) {
            tempoViewBinding.seekBarTempoSelector.progress = it
        }

        tempoViewBinding.seekBarTempoSelector.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        nowPlayingViewModel.saveTempo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            }
        )
        binding.bottomsheetPlayControls.textControlsTempo.setOnClickListener {
            tempoDialog.show()
        }

        nowPlayingViewModel.playbackRate.observe(viewLifecycleOwner) { rate ->
            binding.bottomsheetPlayControls.textControlsTempo.text =
                getString(R.string.tempo_x, rate)
        }

    }

    protected fun showContentError(error: String) {
        Timber.d("Error $error occurred")
    }


    protected fun showProgressLoading(loading: Boolean) {
        Timber.d("Loading $loading")
        //if (loading) binding.progressBarHymnDetailLoading.visibility = View.VISIBLE
        //else binding.progressBarHymnDetailLoading.visibility = View.GONE
    }


    /**
     * Configure the quick settings found in the bottomsheet
     */
    private fun setupQuickSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)
        val defaultTextSize = resources.getInteger(R.integer.normal_detail_text_size).toFloat()
        val fontSizePreference: Preference<Float> = rxPreferences.getFloat(
            getString(R.string.pref_key_detail_font_size), defaultTextSize
        )

        // Example, max_text_size = 6, maxIncrement = 6/20 = 0.3. So we cannot add more than 0.3 to original text size
        val maxTextIncrement = resources.getInteger(R.integer.max_text_size)
        val minTextIncrement = resources.getInteger(R.integer.min_text_size)

        binding.bottomsheetQuickSettings.buttonQuickSettingsFontIncrease.setOnClickListener {
            val currentSize = fontSizePreference.get()
            if (currentSize - defaultTextSize < maxTextIncrement) {
                fontSizePreference.set(currentSize + 1f)
            }
        }

        binding.bottomsheetQuickSettings.buttonQuickSettingsFontDecrease.setOnClickListener {
            val currentSize = fontSizePreference.get()
            if (currentSize - defaultTextSize > minTextIncrement) {
                fontSizePreference.set(currentSize - 1f)
            }
        }

        // night Mode

        quickSettingsViewModel.nightMode.observe(viewLifecycleOwner) { nightMode: NightMode ->
            when (nightMode) {
                NightMode.On -> binding.bottomsheetQuickSettings
                    .radiobuttonQuickSettingsDarkTheme.isChecked = true
                NightMode.Off -> binding.bottomsheetQuickSettings
                    .radiobuttonQuickSettingsLightTheme.isChecked = true
                NightMode.System -> binding.bottomsheetQuickSettings
                    .radiobuttonQuickSettingsSystemTheme.isChecked = true
            }
        }
        binding.bottomsheetQuickSettings.radiogroupQuickSettingsThemeSelector.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radiobutton_quick_settings_dark_theme -> {
                    quickSettingsViewModel.setNightMode(NightMode.On)
                }
                R.id.radiobutton_quick_settings_light_theme -> {
                    quickSettingsViewModel.setNightMode(NightMode.Off)
                }
                R.id.radiobutton_quick_settings_system_theme -> {
                    quickSettingsViewModel.setNightMode(NightMode.System)
                }
            }
        }

        // Display options
        // Set the initial value before listening to further changes
        quickSettingsViewModel.enableSheetMusic.observe(viewLifecycleOwner) { enabled: Boolean ->
            binding.bottomsheetQuickSettings.switchQuickSettingsPreferSheetMusic.apply {
                isChecked = enabled
                setOnCheckedChangeListener { _, isChecked ->
                    quickSettingsViewModel.preferSheetMusic(isChecked)
                }
            }
        }
    }

    private fun showQuickSettingsBottomSheet() {
        //val quickSettingsFragment = QuickSettingsFragment()
        //fragmentManager?.let { quickSettingsFragment.show(it, quickSettingsFragment.tag) }
        if (quickSettingsSheet.state != BottomSheetBehavior.STATE_EXPANDED) {
            quickSettingsSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    fun updateCurrentItemId(itemId: Int) {
        _currentHymnId = itemId
        binding.toolbarDetail.subtitle = "Hymn, $itemId"
    }

    fun updateHymnItems(hymnItems: List<Int>) {
        nowPlayingViewModel.updateHymnItems(hymnItems)
    }

    var currentHymnId
        get() = _currentHymnId
        set(value) {
            _currentHymnId = value
        }

    /**
     * Called upon to request sharing of hymn item
     */
    abstract fun initiateContentSharing()
}

const val EXTRA_CURRENT_ITEM_ID = "currentItemId"
