package com.techbeloved.hymnbook.hymnlisting

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.CATEGORY_PLAYLISTS
import com.techbeloved.hymnbook.utils.CATEGORY_REGEX
import com.techbeloved.hymnbook.utils.CATEGORY_TOPICS
import timber.log.Timber

class HymnListingFragment : BaseHymnListingFragment() {

    private lateinit var viewModel: HymnListingViewModel

    override lateinit var title: String


    override fun navigateToHymnDetail(view: View, item: HymnItemModel) {
        findNavController().navigate(HymnListingFragmentDirections
                .actionHymnListingFragmentToDetailPagerFragment(item.id))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doInOnCreate()
    }


    override fun initViewModel() {
        val factory = HymnListingViewModel.Factory(Injection.provideRepository, Injection.providePlaylistRepo)
        viewModel = ViewModelProviders.of(this, factory).get(HymnListingViewModel::class.java)
    }

    override fun observeViewModel() {
        // Monitor data
        viewModel.hymnTitlesLiveData.observe(viewLifecycleOwner, Observer {
            Timber.i("Receiving items")
            when (it) {
                is Lce.Loading -> showLoadingProgress(it.loading)
                is Lce.Content -> displayContent(it.content)
                is Lce.Error -> showLoadingProgress(false) // Possibly show error message
            }
        })
    }

    private fun doInOnCreate() {
        // Get the topic id from arguments
        val args = arguments?.let { HymnListingFragmentArgs.fromBundle(it) }
        title = args?.title.toString()
        val incomingUri = args?.navUri!!
        Timber.i("incoming Uri: %s", incomingUri)
        val categoryRegex = CATEGORY_REGEX.toRegex()

        if (categoryRegex matches incomingUri) {
            val matchResult = categoryRegex.find(incomingUri)
            val category = matchResult?.groupValues?.get(2)
            val categoryId = matchResult?.groupValues?.get(3)
            Timber.i("Matched category, %s", category)
            when (category) {
                CATEGORY_PLAYLISTS -> viewModel.loadHymnsForPlaylist(categoryId?.toInt() ?: 1)
                CATEGORY_TOPICS -> viewModel.loadHymnsForTopic(categoryId?.toInt() ?: 0)
                else -> viewModel.loadHymnsForTopic()
            }
        }

    }

    override fun loadHymnTitles(sortBy: Int) {
        viewModel.loadHymnTitles(sortBy)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

}