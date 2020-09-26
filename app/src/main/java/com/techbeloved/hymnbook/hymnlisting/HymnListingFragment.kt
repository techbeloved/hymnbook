package com.techbeloved.hymnbook.hymnlisting

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HymnListingFragment : BaseHymnListingFragment() {

    private lateinit var currentCategoryUri: String
    private val viewModel: HymnListingViewModel by viewModels()

    override lateinit var title: String

    private val navController by lazy { findNavController() }

    override fun navigateToHymnDetail(view: View, item: HymnItemModel) {
        navController.safeNavigate(HymnListingFragmentDirections
                .actionHymnListingFragmentToDetailPagerFragment(currentCategoryUri.appendHymnId(item.id)!!))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doInOnCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EXTRA_CURRENT_CATEGORY_URI, currentCategoryUri)
        super.onSaveInstanceState(outState)
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

    private fun doInOnCreate(savedInstanceState: Bundle?) {
        // Get the topic id from arguments

        val args = arguments?.let { HymnListingFragmentArgs.fromBundle(it) }
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_CURRENT_CATEGORY_URI)) {
            currentCategoryUri = savedInstanceState.getString(EXTRA_CURRENT_CATEGORY_URI, DEFAULT_CATEGORY_URI)

        } else {
            currentCategoryUri = args?.navUri ?: DEFAULT_CATEGORY_URI
        }

        title = args?.title.toString()
        Timber.i("incoming Uri: %s", args?.navUri)
        val categoryRegex = CATEGORY_REGEX.toRegex()

        if (categoryRegex matches currentCategoryUri) {
            val matchResult = categoryRegex.find(currentCategoryUri)
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

const val EXTRA_CURRENT_CATEGORY_URI = "currentCategoryUri"
