package com.techbeloved.hymnbook.sheetmusic

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymnlisting.BaseHymnListingFragment
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.safeNavigate
import timber.log.Timber

class SheetMusicListingFragment : BaseHymnListingFragment() {
    override var title: String
        get() = "Sheet Music"
        set(value) {}

    private val navController by lazy { findNavController() }

    override fun navigateToHymnDetail(view: View, item: HymnItemModel) {
        navController.safeNavigate(SheetMusicListingFragmentDirections.actionSheetMusicListingToSheetMusicPagerFragment(item.id))
    }


    private lateinit var viewModel: SheetMusicListingViewModel


    override fun initViewModel() {
        val factory: ViewModelProvider.Factory =
                SheetMusicListingViewModel.Factory(Injection.provideHymnListingUseCases, Injection.provideSchedulers)
        viewModel = ViewModelProviders.of(this, factory)[SheetMusicListingViewModel::class.java]
    }

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