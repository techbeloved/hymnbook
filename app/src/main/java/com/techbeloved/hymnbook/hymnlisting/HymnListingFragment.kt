package com.techbeloved.hymnbook.hymnlisting

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.CATEGORY_PLAYLISTS
import com.techbeloved.hymnbook.utils.CATEGORY_TOPICS
import com.techbeloved.hymnbook.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HymnListingFragment : BaseHymnListingFragment() {

    private val hymnArgs by navArgs<HymnListingFragmentArgs>()
    private val viewModel: HymnListingViewModel by viewModels()

    override lateinit var title: String

    private val navController by lazy { findNavController() }

    override fun navigateToHymnDetail(view: View, item: HymnItemModel) {

        navController.safeNavigate(
            HymnListingFragmentDirections
                .actionHymnListingFragmentToDetailPagerFragment(
                    hymnId = item.id,
                    category = hymnArgs.category,
                    categoryId = hymnArgs.categoryId
                )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doInOnCreate()
    }

    override fun observeViewModel() {
        // Monitor data
        viewModel.hymnTitlesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Lce.Loading -> showLoadingProgress(it.loading)
                is Lce.Content -> displayContent(it.content)
                is Lce.Error -> showLoadingProgress(false) // Possibly show error message
            }
        }
    }

    private fun doInOnCreate() {

        title = hymnArgs.title

        when (hymnArgs.category) {
            CATEGORY_PLAYLISTS -> viewModel.loadHymnsForPlaylist(hymnArgs.categoryId)
            CATEGORY_TOPICS -> viewModel.loadHymnsForTopic(hymnArgs.categoryId)
            else -> viewModel.loadHymnsForTopic()
        }

    }

    override fun loadHymnTitles(sortBy: Int) {
        viewModel.loadHymnTitles(sortBy)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

}
