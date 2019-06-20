package com.techbeloved.hymnbook.hymndetail


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.DEFAULT_CATEGORY_URI
import com.techbeloved.hymnbook.utils.DepthPageTransformer
import com.techbeloved.hymnbook.utils.hymnId
import com.techbeloved.hymnbook.utils.parentCategoryUri
import timber.log.Timber

class DetailPagerFragment : BaseDetailPagerFragment() {

    private lateinit var viewModel: HymnPagerViewModel
    private lateinit var detailPagerAdapter: DetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore current index
        if (savedInstanceState != null) {
            currentHymnId = savedInstanceState.getInt(EXTRA_CURRENT_ITEM_ID, 1)
            currentCategoryUri = savedInstanceState.getString(EXTRA_CURRENT_CATEGORY_URI, DEFAULT_CATEGORY_URI)
        } else {
            val args = arguments?.let { DetailPagerFragmentArgs.fromBundle(it) }
            val inComingItemUri = args?.navUri!!
            currentHymnId = inComingItemUri.hymnId()?.toInt() ?: 1
            currentCategoryUri = inComingItemUri.parentCategoryUri() ?: DEFAULT_CATEGORY_URI
        }

        val factory = HymnPagerViewModel.Factory(Injection.provideRepository,
                Injection.providePlaylistRepo,
                currentCategoryUri, Injection.provideSchedulers)
        viewModel = ViewModelProviders.of(this, factory).get(HymnPagerViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailPagerAdapter = DetailPagerAdapter(childFragmentManager)
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())

        viewModel.hymnIndicesLiveData.observe(viewLifecycleOwner, Observer {
            val indexToLoad = currentHymnId
            when (it) {
                is Lce.Loading -> showProgressLoading(it.loading)
                is Lce.Content -> {
                    initializeViewPager(it.content, indexToLoad)
                    updateCurrentItemId(indexToLoad)
                    updateHymnItems(it.content)
                }
                is Lce.Error -> showContentError(it.error)
            }
        })

        viewModel.header.observe(viewLifecycleOwner, Observer { title ->
            binding.toolbarDetail.title = title
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EXTRA_CURRENT_ITEM_ID, currentHymnId)
        outState.putString(EXTRA_CURRENT_CATEGORY_URI, currentCategoryUri)
        super.onSaveInstanceState(outState)
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
