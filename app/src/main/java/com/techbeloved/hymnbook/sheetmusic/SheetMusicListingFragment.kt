package com.techbeloved.hymnbook.sheetmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentSongListingBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.hymnlisting.HymnListAdapter
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SheetMusicListingFragment : Fragment() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private lateinit var hymnListAdapter: HymnListAdapter

    private val clickListener = object : HymnItemModel.ClickListener<HymnItemModel> {
        override fun onItemClick(item: HymnItemModel) {
            navigateToHymnDetail(item.id)
        }
    }

    private lateinit var viewModel: SheetMusicListingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory: ViewModelProvider.Factory =
                SheetMusicListingViewModel.Factory(Injection.provideOnlineRepo().value, Injection.provideAppContext(), Injection.provideMediaAssetUseCases)
        viewModel = ViewModelProviders.of(this, factory)[SheetMusicListingViewModel::class.java]
        viewModel.loadHymnTitlesFromRepo()

        shouldDownloadCatalogIfFirstStart()
    }

    private fun shouldDownloadCatalogIfFirstStart() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)

        val isFirstStart: Preference<Boolean> = rxPreferences.getBoolean(getString(R.string.pref_key_first_start), true)
        isFirstStart.asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ isFirst ->
                    if (isFirst) {
                        scheduleCatalogDownload()
                    }
                }, { Timber.w("Some error occurred")}).run { disposables.add(this) }
    }

    private fun scheduleCatalogDownload() {
        viewModel.getLatestCatalogue()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
    }

    private lateinit var binding: FragmentSongListingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_listing, container, false)

        hymnListAdapter = HymnListAdapter(clickListener)
        binding.recyclerviewSongList.apply {
            adapter = hymnListAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        binding.toolbarSongListing.setTitle(R.string.label_hymns)

        setupViewModel()

        return binding.root
    }

    private fun setupViewModel() {
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

    private fun displayContent(content: List<TitleItem>) {
        hymnListAdapter.submitData(content)
        showLoadingProgress(false)
    }

    private fun showLoadingProgress(loading: Boolean) {
        if (loading) binding.progressBarSongsLoading.visibility = View.VISIBLE
        else binding.progressBarSongsLoading.visibility = View.GONE
    }

    private fun navigateToHymnDetail(hymnId: Int) {
        findNavController().navigate(SheetMusicListingFragmentDirections.actionSheetMusicListingToSheetMusicPagerFragment(hymnId))
    }
}