package com.techbeloved.hymnbook.sheetmusic

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.techbeloved.hymnbook.hymnlisting.BaseHymnListingFragment
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SheetMusicListingFragment : BaseHymnListingFragment() {
    override var title: String
        get() = "Sheet Music"
        set(value) {}

    private val navController by lazy { findNavController() }

    override fun navigateToHymnDetail(view: View, item: HymnItemModel) {
        navController.safeNavigate(SheetMusicListingFragmentDirections.actionSheetMusicListingToSheetMusicPagerFragment(item.id))
    }


    private val viewModel: SheetMusicListingViewModel by viewModels()

    override fun observeViewModel() {
        // Monitor data
        viewModel.hymnTitlesLce.observe(viewLifecycleOwner, Observer {
            Timber.i("Receiving items")
            when (it) {
                is Lce.Loading -> showLoadingProgress(it.loading)
                is Lce.Content -> displayContent(it.content)
                is Lce.Error -> showLoadingProgress(false) // Possibly show error message
            }
        })
    }

    override fun loadHymnTitles(sortBy: Int) {
        viewModel.loadHymnTitlesFromRepo(sortBy)
    }
}