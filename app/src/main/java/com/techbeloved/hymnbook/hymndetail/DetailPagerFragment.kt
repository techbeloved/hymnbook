package com.techbeloved.hymnbook.hymndetail


import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_pager, container, false)
        binding.lifecycleOwner = this

        setupImmersiveMode() // Immersive mode

        NavigationUI.setupWithNavController(binding.toolbarDetail, findNavController())

        detailPagerAdapter = DetailPagerAdapter(childFragmentManager)
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())

        val args = arguments?.let { DetailPagerFragmentArgs.fromBundle(it) }
        initialIndex = args?.hymnId ?: 1

        return binding.root
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

}
