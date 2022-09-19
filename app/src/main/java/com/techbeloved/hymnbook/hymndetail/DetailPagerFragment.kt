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
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.snackbar.Snackbar
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.model.HymnNumber
import com.techbeloved.hymnbook.data.model.NewFeature
import com.techbeloved.hymnbook.sheetmusic.SheetMusicDetailFragment
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.DepthPageTransformer
import com.techbeloved.hymnbook.utils.MINIMUM_VERSION_FOR_SHARE_LINK
import com.techbeloved.hymnbook.utils.WCCRM_LOGO_URL
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.properties.Delegates.observable

@AndroidEntryPoint
class DetailPagerFragment : BaseDetailPagerFragment() {

    private val detailArgs by navArgs<DetailPagerFragmentArgs>()
    private val viewModel: HymnPagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore current index
        currentHymnId = savedInstanceState?.getInt(EXTRA_CURRENT_ITEM_ID, detailArgs.hymnId)
            ?: detailArgs.hymnId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val detailPagerAdapter = DetailPagerAdapter(childFragmentManager).also { adapter ->
            viewModel.preferSheetMusic.observe(viewLifecycleOwner) { adapter.preferSheetMusic = it }
        }
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())

        viewModel.hymnIndicesLiveData.observe(viewLifecycleOwner) { indicesLce ->
            val indexToLoad = currentHymnId
            when (indicesLce) {
                is Lce.Loading -> showProgressLoading(indicesLce.loading)
                is Lce.Content -> {
                    initializeViewPager(detailPagerAdapter, indicesLce.content, indexToLoad)
                    updateCurrentItemId(indexToLoad)
                    updateHymnItems(indicesLce.content.map { it.number })
                }
                is Lce.Error -> showContentError(indicesLce.error)
            }
        }

        viewModel.header.observe(viewLifecycleOwner) { title ->
            binding.toolbarDetail.title = title
        }

        viewModel.shareLinkStatus.observe(viewLifecycleOwner) { shareStatus ->
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
        }

        viewModel.newFeatures.observe(viewLifecycleOwner) { feature ->
            when (feature) {
                NewFeature.SheetMusic ->
                    showNewFeatureHighlight(feature, getString(R.string.sheet_music_discovery))
                null -> {
                    // Nothing to do
                }
            }
        }
    }

    private fun showShareError(error: Throwable) {
        Timber.w(error)
        Snackbar.make(
            requireView().rootView,
            "Failure creating share content",
            Snackbar.LENGTH_SHORT
        ).show()
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
        super.onSaveInstanceState(outState)
    }

    private fun initializeViewPager(
        detailPagerAdapter: DetailPagerAdapter,
        hymnIndices: List<HymnNumber>,
        initialIndex: Int
    ) {
        Timber.i("Initializing viewPager with index: $initialIndex")
        //showProgressLoading(false)

        detailPagerAdapter.submitList(hymnIndices)
        // initialIndex represents the hymn number, where as the adapter uses a zero based index
        // Which implies that when the indices is sorted by titles, the correct detail won't be shown.
        // So we just need to find the index from the list of hymn indices

        val indexToLoad = hymnIndices.indexOfFirst { it.number == initialIndex }
        binding.viewpagerHymnDetail.currentItem = indexToLoad
    }

    override fun initiateContentSharing() {
        viewModel.requestShareLink(
            currentHymnId,
            getString(R.string.about_app),
            MINIMUM_VERSION_FOR_SHARE_LINK,
            WCCRM_LOGO_URL
        )
    }

    override fun newFeatureShown(feature: NewFeature) {
        viewModel.newFeatureShown(feature)
    }

    @SuppressLint("WrongConstant")
    inner class DetailPagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val hymnIndices = mutableListOf<HymnNumber>()
        var preferSheetMusic: Boolean by observable(false) { property, oldValue, newValue ->
            if (oldValue != newValue) notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment {
            val item = hymnIndices[position]
            return if (preferSheetMusic && item.hasSheetMusic) {
                SheetMusicDetailFragment().apply { init(item.number) }
            } else {
                DetailFragment().apply { init(item.number) }
            }
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            updateCurrentItemId(hymnIndices[position].number)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            return hymnIndices.size
        }

        fun submitList(hymnIndices: List<HymnNumber>) {
            this.hymnIndices.clear()
            this.hymnIndices.addAll(hymnIndices)
            notifyDataSetChanged()
        }

    }

}
