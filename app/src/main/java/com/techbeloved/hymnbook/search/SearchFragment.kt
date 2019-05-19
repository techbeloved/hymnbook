package com.techbeloved.hymnbook.search

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentSearchBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.usecases.Lce
import timber.log.Timber

class SearchFragment : Fragment() {

    private val clickListener: HymnItemModel.ClickListener<HymnItemModel> = object : HymnItemModel.ClickListener<HymnItemModel> {
        override fun onItemClick(item: HymnItemModel) {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToDetailPagerFragment(item.id))
        }

    }

    private val searchQueryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            searchHymns(query ?: "")
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }

    }

    private lateinit var viewModel: SearchViewModel

    private lateinit var binding: FragmentSearchBinding

    lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this
        NavigationUI.setupWithNavController(binding.toolbarSearch, findNavController())

        binding.searchviewSearch.setOnQueryTextListener(searchQueryListener)
        binding.searchviewSearch.setOnFocusChangeListener { view, hasFocus -> if (!hasFocus) hideKeyboard(view) }

        searchResultsAdapter = SearchResultsAdapter(clickListener)

        binding.recyclerviewSearchResults.apply {
            adapter = searchResultsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        binding.recyclerviewSearchResults.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = SearchViewModel.Factory(Injection.provideRepository)
        viewModel = ViewModelProviders.of(this, factory).get(SearchViewModel::class.java)
        viewModel.monitorSearch()

        viewModel.searchResults.observe(this, Observer {
            when (it) {
                is Lce.Loading -> showLoadingProgress(it.loading)
                is Lce.Content -> showResults(it.content)
                is Lce.Error -> showError(it.error)
            }
        })
    }

    private fun searchHymns(query: String) {
        viewModel.search(query)
    }

    private fun showError(error: String) {
        showLoadingProgress(false)
        Timber.e(error)
    }

    private fun showResults(content: List<SearchResultItem>) {
        showLoadingProgress(false)
        searchResultsAdapter.submitList(content)
    }

    private fun showLoadingProgress(loading: Boolean) {
        if (loading) binding.progressbarSearchLoading.visibility = View.VISIBLE
        else binding.progressbarSearchLoading.visibility = View.GONE
    }

    private fun hideKeyboard(view: View) {
        if (activity != null) {
            val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
