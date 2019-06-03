package com.techbeloved.hymnbook.hymndetail


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
import com.techbeloved.hymnbook.utils.DepthPageTransformer
import timber.log.Timber

class DetailPagerFragment : BaseDetailPagerFragment() {

    private lateinit var viewModel: HymnPagerViewModel
    private lateinit var detailPagerAdapter: DetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = HymnPagerViewModel.Factory(Injection.provideRepository)
        viewModel = ViewModelProviders.of(this, factory).get(HymnPagerViewModel::class.java)

        // Restore current index
        if (savedInstanceState != null) {
            currentHymnId = savedInstanceState.getInt(CURRENT_ITEM_ID, 1)
        } else {
            val args = arguments?.let { DetailPagerFragmentArgs.fromBundle(it) }
            currentHymnId = args?.hymnId ?: 1
        }

        viewModel.hymnIndicesLiveData.observe(this, Observer {
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

        viewModel.loadHymnIndices()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailPagerAdapter = DetailPagerAdapter(childFragmentManager)
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())
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

    inner class DetailPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
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
