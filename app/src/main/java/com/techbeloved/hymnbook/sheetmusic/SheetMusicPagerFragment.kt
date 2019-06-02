package com.techbeloved.hymnbook.sheetmusic

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymndetail.BaseDetailPagerFragment
import com.techbeloved.hymnbook.hymndetail.CURRENT_ITEM_ID
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.DepthPageTransformer
import timber.log.Timber

class SheetMusicPagerFragment : BaseDetailPagerFragment() {
    private lateinit var viewModel: SheetMusicPagerViewModel
    private lateinit var detailPagerAdapter: DetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore current index
        if (savedInstanceState != null) {
            currentHymnId = savedInstanceState.getInt(CURRENT_ITEM_ID, 1)
        } else {
            val args = arguments?.let { SheetMusicPagerFragmentArgs.fromBundle(it) }
            currentHymnId = args?.hymnId ?: 1
        }

        val factory: ViewModelProvider.Factory = SheetMusicPagerViewModel.Factory(Injection.provideOnlineRepo)
        viewModel = ViewModelProviders.of(this, factory)[SheetMusicPagerViewModel::class.java]
        viewModel.hymnIndicesLive.observe(this, Observer {
            when (it) {
                is Lce.Loading -> showProgressLoading(it.loading)
                is Lce.Content -> initializeViewPager(it.content, currentHymnId)
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