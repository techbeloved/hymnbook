package com.techbeloved.hymnbook.hymnlisting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentSongListingBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymndetail.DetailFragment
import com.techbeloved.hymnbook.hymndetail.DetailPagerFragment
import com.techbeloved.hymnbook.usecases.Lce
import timber.log.Timber

class HymnListingFragment: Fragment() {
    private lateinit var binding: FragmentSongListingBinding
    private lateinit var viewModel: HymnListingViewModel
    private lateinit var hymnListAdapter: HymnListAdapter

    private val clickListener = object : HymnItemModel.ClickListener<HymnItemModel> {
        override fun onItemClick(item: HymnItemModel) {
            navigateToHymnDetail(item.id)
        }
    }

    private fun navigateToHymnDetail(hymnIndex: Int) {
        val fragment = DetailPagerFragment()
        fragment.init(hymnIndex)

        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.containerMain, fragment)
                ?.addToBackStack(null)
                ?.commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_song_listing, container, false)
        binding.lifecycleOwner = this

        hymnListAdapter = HymnListAdapter(clickListener)
        binding.recyclerviewSongList.apply {
            adapter = hymnListAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = HymnListingViewModel.Factory(Injection.provideRepository())
        viewModel = ViewModelProviders.of(this, factory).get(HymnListingViewModel::class.java)
        Timber.i("onActivityCreated: Hymn listing. ViewModel: $viewModel")

        // Monitor data
        viewModel.hymnTitlesLiveData.observe(this, Observer {
            when (it) {
                is Lce.Loading -> showLoadingProgress(it.loading)
                is Lce.Content -> displayContent(it.content)
                is Lce.Error -> showLoadingProgress(false) // Possibly show error message
            }
        })

        // Load the hymn titles
        viewModel.loadHymnTitles()
    }


    private fun displayContent(content: List<TitleItem>) {
        hymnListAdapter.submitList(content)
        showLoadingProgress(false)
    }

    private fun showLoadingProgress(loading: Boolean) {
        if (loading) binding.progressBarSongsLoading.visibility = View.VISIBLE
        else binding.progressBarSongsLoading.visibility = View.GONE
    }

}