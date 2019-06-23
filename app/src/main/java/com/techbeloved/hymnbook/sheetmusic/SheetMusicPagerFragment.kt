package com.techbeloved.hymnbook.sheetmusic

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymndetail.BaseDetailPagerFragment
import com.techbeloved.hymnbook.hymndetail.EXTRA_CURRENT_ITEM_ID
import com.techbeloved.hymnbook.hymndetail.ShareStatus
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.DepthPageTransformer
import com.techbeloved.hymnbook.utils.MINIMUM_VERSION_FOR_SHARE_LINK
import com.techbeloved.hymnbook.utils.WCCRM_LOGO_URL
import timber.log.Timber

class SheetMusicPagerFragment : BaseDetailPagerFragment() {
    private lateinit var viewModel: SheetMusicPagerViewModel
    private lateinit var detailPagerAdapter: DetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore current index
        if (savedInstanceState != null) {
            currentHymnId = savedInstanceState.getInt(EXTRA_CURRENT_ITEM_ID, 1)
        } else {
            val args = arguments?.let { SheetMusicPagerFragmentArgs.fromBundle(it) }
            currentHymnId = args?.hymnId ?: 1
        }

        val factory: ViewModelProvider.Factory = SheetMusicPagerViewModel.Factory(
                Injection.provideOnlineRepo,
                Injection.shareLinkProvider,
                Injection.provideSchedulers)

        viewModel = ViewModelProviders.of(this, factory)[SheetMusicPagerViewModel::class.java]
        viewModel.hymnIndicesLive.observe(this, Observer {
            when (it) {
                is Lce.Loading -> showProgressLoading(it.loading)
                is Lce.Content -> {
                    initializeViewPager(it.content, currentHymnId)
                    updateHymnItems(it.content)
                }
                is Lce.Error -> showContentError(it.error)
            }
        })
        viewModel.loadIndices()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailPagerAdapter = DetailPagerAdapter(childFragmentManager)
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())

        viewModel.shareLinkStatus.observe(viewLifecycleOwner, Observer { shareStatus ->
            when (shareStatus) {
                ShareStatus.Loading -> showShareLoadingDialog()
                is ShareStatus.Success -> showShareOptionsChooser(shareStatus.shareLink)
                is ShareStatus.Error -> {
                    showShareError(shareStatus.error)
                }
                ShareStatus.None -> {
                    cancelProgressDialog()
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EXTRA_CURRENT_ITEM_ID, currentHymnId)
        super.onSaveInstanceState(outState)
    }

    override fun initiateContentSharing() {
        viewModel.requestShareLink(
                currentHymnId,
                getString(R.string.about_app),
                MINIMUM_VERSION_FOR_SHARE_LINK,
                WCCRM_LOGO_URL)
    }

    private fun showShareError(error: Throwable) {
        Timber.w(error)
        Snackbar.make(requireView().rootView, "Failure creating share content", Snackbar.LENGTH_SHORT).show()
    }

    private fun showShareOptionsChooser(shareLink: String) {
        ShareCompat.IntentBuilder.from(requireActivity()).apply {
            setChooserTitle(getString(R.string.share_hymn))
            setType("text/plain")
            setText(shareLink)
        }.startChooser()

    }

    private var progressDialog: ProgressDialog? = null
    private fun showShareLoadingDialog() {
        progressDialog = ProgressDialog.show(requireContext(), "Share hymn", "Working")
        progressDialog?.setCancelable(true)
    }

    private fun cancelProgressDialog() {
        progressDialog?.cancel()
    }

    private fun initializeViewPager(hymnIndices: List<Int>, initialIndex: Int) {
        Timber.i("Initializing viewPager with index: $initialIndex")
        //showProgressLoading(false)

        detailPagerAdapter.submitList(hymnIndices)
        // initialIndex represents the hymn number, where as the adapter uses a zero based index
        // Which implies that when the indices is sorted by titles, the correct detail won't be shown.
        // So we just need to find the index from the list of hymn indices

        val indexToLoad = hymnIndices.indexOf(initialIndex)
        binding.viewpagerHymnDetail.currentItem = indexToLoad
    }

    @SuppressLint("WrongConstant")
    inner class DetailPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
            updateCurrentItemId(hymnIndices[position])
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
}