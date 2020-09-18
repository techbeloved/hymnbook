package com.techbeloved.hymnbook.hymnlisting

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentSongListingBinding
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.BY_TITLE
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseHymnListingFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    abstract var title: String
    private lateinit var hymnListAdapter: HymnListAdapterNoDiff

    abstract fun initViewModel()

    /**
     * Load hymn titles sorted by the key
     * @param sortBy sort key
     */
    abstract fun loadHymnTitles(@SortBy sortBy: Int)

    /**
     * Display content, usually submit to list adapter
     */
    protected fun displayContent(content: List<TitleItem>) {
        hymnListAdapter.submitData(content)
        showLoadingProgress(false)
    }

    /**
     * Show some UI loading animation
     */
    protected fun showLoadingProgress(loading: Boolean) {
        if (loading) binding.progressBarSongsLoading.visibility = View.VISIBLE
        else binding.progressBarSongsLoading.visibility = View.GONE
    }

    abstract fun navigateToHymnDetail(view: View, item: HymnItemModel)


    private val disposables = CompositeDisposable()

    /**
     * Hymn filter watcher
     */
    protected val filterHymnTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            filterPublishSubject.onNext(s?.toString() ?: "")

        }
    }

    private val clickListener = object : HymnItemModel.ClickListener<HymnItemModel> {
        override fun onItemClick(view: View, item: HymnItemModel) {
            navigateToHymnDetail(view, item)
        }
    }

    private val filterPublishSubject: PublishSubject<String> = PublishSubject.create()

    private lateinit var preferences: SharedPreferences
    private lateinit var rxPreferences: RxSharedPreferences
    private lateinit var sortByPref: Preference<Int>


    private fun setupFilterObserver() {
        val disposable = filterPublishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .skip(1)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ keyword -> hymnListAdapter.filter.filter(keyword) }, { Timber.w(it, "Could not do filter") })
        disposables.add(disposable)
    }

    protected fun hideKeyboard(view: View) {
        if (activity != null) {
            val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private var currentSortKey = R.id.action_sort_by_number
    protected fun showSortByPopup(view: View) {
        val sortByPopup = PopupMenu(context!!, view, Gravity.END)
        sortByPopup.inflate(R.menu.filter_menu)
        sortByPopup.menu.findItem(currentSortKey).isChecked = true
        sortByPopup.show()
        sortByPopup.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_by_number -> sortByPref.set(BY_NUMBER)
            R.id.action_sort_by_title -> sortByPref.set(BY_TITLE)
            else -> return false
        }
        // Finally set checked the item
        item.isChecked = true
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)
        rxPreferences = RxSharedPreferences.create(preferences)

        sortByPref = rxPreferences.getInteger(getString(R.string.pref_key_sort_by))

        initViewModel()

        setupFilterObserver()

        val disposable = sortByPref.asObservable().subscribe(
                {
                    Timber.i("Current settings: %s", it)
                    currentSortKey = when (it) {
                        BY_TITLE -> R.id.action_sort_by_title
                        else -> R.id.action_sort_by_number
                    }
                    loadHymnTitles(it)
                }, { Timber.e(it) })
        disposables.add(disposable)
    }

    private lateinit var binding: FragmentSongListingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_listing, container, false)
        binding.lifecycleOwner = this
        NavigationUI.setupWithNavController(binding.toolbarSongListing, findNavController())
        binding.toolbarSongListing.title = title

        hymnListAdapter = HymnListAdapterNoDiff(clickListener)
        binding.recyclerviewSongList.apply {
            adapter = hymnListAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        binding.edittextFilterHymns.addTextChangedListener(filterHymnTextWatcher)
        binding.edittextFilterHymns.setSortByClickListener {
            showSortByPopup(it)
        }
        binding.edittextFilterHymns.setOnFocusChangeListener { view, hasFocus -> if (!hasFocus) hideKeyboard(view) }

        observeViewModel()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Clear the filter
        binding.edittextFilterHymns.text?.let {
            if (it.isNotBlank()) {
                binding.edittextFilterHymns.setText("")
            }
        }

    }

    /**
     * Start to observe for changes in the viewModel
     */
    abstract fun observeViewModel()

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
    }
}