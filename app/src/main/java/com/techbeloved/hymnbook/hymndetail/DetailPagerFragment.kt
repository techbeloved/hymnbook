package com.techbeloved.hymnbook.hymndetail


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.snackbar.Snackbar
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.sheetmusic.SheetMusicDetailFragment
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.properties.Delegates.observable

@AndroidEntryPoint
class DetailPagerFragment : BaseDetailPagerFragment() {

    private val viewModel: HymnPagerViewModel by viewModels()
    private lateinit var detailPagerAdapter: DetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore current index
        if (savedInstanceState != null) {
            currentHymnId = savedInstanceState.getInt(EXTRA_CURRENT_ITEM_ID, 1)
            currentCategoryUri = savedInstanceState.getString(EXTRA_CURRENT_CATEGORY_URI, DEFAULT_CATEGORY_URI)
        } else {
            val args = requireArguments().let { DetailPagerFragmentArgs.fromBundle(it) }
            val inComingItemUri = args.navUri
            currentHymnId = inComingItemUri.hymnId()?.toInt() ?: 1
            currentCategoryUri = inComingItemUri.parentCategoryUri() ?: DEFAULT_CATEGORY_URI
        }
        // Set the category uri. This would be used by the viewModel
        requireArguments().putString(HymnPagerViewModel.CATEGORY_URI_ARG, currentCategoryUri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailPagerAdapter = DetailPagerAdapter(childFragmentManager).also { adapter ->
            viewModel.preferSheetMusic.observe(viewLifecycleOwner) { adapter.preferSheetMusic = it }
        }
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())

        viewModel.hymnIndicesLiveData.observe(viewLifecycleOwner, Observer { indicesLce ->
            val indexToLoad = currentHymnId
            when (indicesLce) {
                is Lce.Loading -> showProgressLoading(indicesLce.loading)
                is Lce.Content -> {
                    initializeViewPager(indicesLce.content, indexToLoad)
                    updateCurrentItemId(indexToLoad)
                    updateHymnItems(indicesLce.content.map { it.first })
                }
                is Lce.Error -> showContentError(indicesLce.error)
            }
        })

        viewModel.header.observe(viewLifecycleOwner, Observer { title ->
            binding.toolbarDetail.title = title
        })

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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EXTRA_CURRENT_ITEM_ID, currentHymnId)
        outState.putString(EXTRA_CURRENT_CATEGORY_URI, currentCategoryUri)
        super.onSaveInstanceState(outState)
    }

    private fun initializeViewPager(hymnIndices: List<Pair<Int, Boolean>>, initialIndex: Int) {
        Timber.i("Initializing viewPager with index: $initialIndex")
        //showProgressLoading(false)

        detailPagerAdapter.submitList(hymnIndices)
        // initialIndex represents the hymn number, where as the adapter uses a zero based index
        // Which implies that when the indices is sorted by titles, the correct detail won't be shown.
        // So we just need to find the index from the list of hymn indices

        val indexToLoad = hymnIndices.indexOfFirst { it.first == initialIndex }
        binding.viewpagerHymnDetail.currentItem = indexToLoad
    }

    override fun initiateContentSharing() {
        viewModel.requestShareLink(currentHymnId,
                getString(R.string.about_app),
                MINIMUM_VERSION_FOR_SHARE_LINK,
                WCCRM_LOGO_URL)
    }

    @SuppressLint("WrongConstant")
    inner class DetailPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val hymnIndices = mutableListOf<Pair<Int, Boolean>>()
        var preferSheetMusic: Boolean by observable(false) { property, oldValue, newValue ->
            if (oldValue != newValue) notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment {
            val item = hymnIndices[position]
            val hymnToShow = if (position < hymnIndices.size) item.first else 1
            return if (preferSheetMusic && item.second) {
                SheetMusicDetailFragment().apply { init(hymnToShow) }
            } else {
                DetailFragment().apply { init(hymnToShow) }
            }
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            updateCurrentItemId(hymnIndices[position].first)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            return hymnIndices.size
        }

        fun submitList(hymnIndices: List<Pair<Int, Boolean>>) {
            this.hymnIndices.clear()
            this.hymnIndices.addAll(hymnIndices)
            notifyDataSetChanged()
        }

    }

}
