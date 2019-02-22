package com.techbeloved.hymnbook.hymndetail


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentDetailPagerBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.DepthPageTransformer
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
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_detail_pager, container, false)
        binding.lifecycleOwner = this

        detailPagerAdapter = DetailPagerAdapter(childFragmentManager)
        binding.viewpagerHymnDetail.adapter = detailPagerAdapter
        binding.viewpagerHymnDetail.setPageTransformer(true, DepthPageTransformer())

        // Load initial page supplied from the listing fragment
        initialIndex = arguments?.getInt(ARG_INITIAL_INDEX, 1) ?: 1

        return binding.root
    }

    private var initialIndex: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = HymnPagerViewModel.Factory(Injection.provideRepository())
        viewModel = ViewModelProviders.of(this, factory).get(HymnPagerViewModel::class.java)
        Timber.i("onActivityCreated")
        viewModel.hymnIndicesLiveData.observe(this, Observer {
            when(it) {
                is Lce.Loading -> showProgressLoading(it.loading)
                is Lce.Content -> initializeViewPager(it.content, initialIndex)
                is Lce.Error -> showContentError(it.error)
            }
        })

        viewModel.loadHymnIndices()
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

    fun init(hymnIndex: Int) {
        val args = Bundle()
        args.putInt(ARG_INITIAL_INDEX, hymnIndex)
        this.arguments = args
    }

    companion object {
        const val ARG_INITIAL_INDEX = "initialHymnIndex"
    }

    inner class DetailPagerAdapter(private val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val hymnIndices = mutableListOf<Int>()
        override fun getItem(position: Int): Fragment? {
            return if (position < hymnIndices.size)  {
                val detailFragment = DetailFragment()
                detailFragment.init(hymnIndices[position])
                detailFragment
            }
            else null
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
