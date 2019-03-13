package com.techbeloved.hymnbook.hymnlisting

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.edittextwithsortby.FilterByEditText
import com.techbeloved.edittextwithsortby.SortByEditText
import com.techbeloved.hymnbook.HymnbookViewModel
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentSongListingBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class HymnListingFragment : Fragment() {
    private lateinit var binding: FragmentSongListingBinding
    private lateinit var viewModel: HymnListingViewModel
    private lateinit var hymnListAdapter: HymnListAdapter

    private val clickListener = object : HymnItemModel.ClickListener<HymnItemModel> {
        override fun onItemClick(item: HymnItemModel) {
            navigateToHymnDetail(item.id)
        }
    }

    private fun navigateToHymnDetail(hymnIndex: Int) {
        findNavController().navigate(HymnListingFragmentDirections.actionHymnListingFragmentToDetailPagerFragment(hymnIndex))
    }

    private val filterHymnTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            filterPublishSubject.onNext(s?.toString() ?: "")

        }
    }

    private lateinit var preferences: SharedPreferences
    private lateinit var rxPreferences: RxSharedPreferences
    private lateinit var sortByPref: Preference<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        rxPreferences = RxSharedPreferences.create(preferences)

        sortByPref = rxPreferences.getInteger(getString(R.string.pref_key_sort_by), BY_NUMBER)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_listing, container, false)
        binding.lifecycleOwner = this
        NavigationUI.setupWithNavController(binding.toolbarSongListing, findNavController())

        hymnListAdapter = HymnListAdapter(clickListener)
        binding.recyclerviewSongList.apply {
            adapter = hymnListAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        binding.edittextFilterHymns.addTextChangedListener(filterHymnTextWatcher)
        binding.edittextFilterHymns.setSortByClickListener { Toast.makeText(context, "Sort by icon clicked!", Toast.LENGTH_SHORT).show() }
        binding.edittextFilterHymns.setOnFocusChangeListener { view, hasFocus -> if (!hasFocus) hideKeyboard(view) }

        setupFilterObserver()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Clear the filter
        binding.edittextFilterHymns.setText("")
    }

    private lateinit var mainViewModel: HymnbookViewModel
    private val disposables = CompositeDisposable()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = HymnListingViewModel.Factory(Injection.provideRepository())
        viewModel = ViewModelProviders.of(this, factory).get(HymnListingViewModel::class.java)

        mainViewModel = ViewModelProviders.of(activity!!).get(HymnbookViewModel::class.java)
        // Monitor data
        viewModel.hymnTitlesLiveData.observe(this, Observer {
            when (it) {
                is Lce.Loading -> showLoadingProgress(it.loading)
                is Lce.Content -> displayContent(it.content)
                is Lce.Error -> showLoadingProgress(false) // Possibly show error message
            }
        })

        val disposable = sortByPref.asObservable().subscribe(
                { viewModel.loadHymnTitles(it) }, { Timber.e(it) })
        disposables.add(disposable)

    }


    private fun displayContent(content: List<TitleItem>) {
        hymnListAdapter.submitData(content)
        showLoadingProgress(false)
    }

    private fun showLoadingProgress(loading: Boolean) {
        if (loading) binding.progressBarSongsLoading.visibility = View.VISIBLE
        else binding.progressBarSongsLoading.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
    }

    private fun hideKeyboard(view: View) {
        if (activity != null) {
            val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private val filterPublishSubject: PublishSubject<String> = PublishSubject.create()


    private fun setupFilterObserver() {
        val disposable = filterPublishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .startWith("")
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ keyword -> hymnListAdapter.filter.filter(keyword) }, { Timber.w(it, "Could not do filter") })
        disposables.add(disposable)
    }

}