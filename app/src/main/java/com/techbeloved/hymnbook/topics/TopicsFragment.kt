package com.techbeloved.hymnbook.topics

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.TopicsFragmentBinding
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.hymnlisting.HymnListAdapter
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.AUTHORITY
import com.techbeloved.hymnbook.utils.CATEGORY_TOPICS
import com.techbeloved.hymnbook.utils.SCHEME_NORMAL
import com.techbeloved.hymnbook.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TopicsFragment : Fragment() {

    private val navController by lazy { findNavController() }
    private val topicClickListener: HymnItemModel.ClickListener<HymnItemModel> =
            object : HymnItemModel.ClickListener<HymnItemModel> {
                override fun onItemClick(view: View, item: HymnItemModel) {
                    val navUri = Uri.Builder()
                            .authority(AUTHORITY)
                            .scheme(SCHEME_NORMAL)
                            .appendEncodedPath(CATEGORY_TOPICS)
                            .appendEncodedPath(item.id.toString())
                            .build()
                    Timber.i("navUri: %s", navUri)
                    navController.safeNavigate(TopicsFragmentDirections
                            .actionTopicsFragmentToHymnListingFragment(item.title, navUri.toString()))
                }
            }

    private val viewModel: TopicsViewModel by viewModels()

    private lateinit var binding: TopicsFragmentBinding

    private val topicsAdapter: HymnListAdapter by lazy {
        HymnListAdapter(topicClickListener)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.topics_fragment, container, false)
        NavigationUI.setupWithNavController(binding.toolbarTopics, findNavController())

        binding.recyclerviewTopicList.apply {
            adapter = topicsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.allTopicsLiveData.observe(viewLifecycleOwner, Observer { topicsLce ->
            when (topicsLce) {
                is Lce.Loading -> showUiLoading(topicsLce.loading)
                is Lce.Content -> displayContent(topicsLce.content)
                is Lce.Error -> showError(topicsLce.error)
            }
        })
    }

    private fun showError(error: String) {
        Snackbar.make(binding.coordinatorLayoutTopics.rootView, error, Snackbar.LENGTH_SHORT)
        showUiLoading(false)
    }

    private fun displayContent(topics: List<TopicItem>) {
        topicsAdapter.submitData(topics)
        showUiLoading(false)
    }

    private fun showUiLoading(loading: Boolean) {
        binding.progressBarSongsLoading.isVisible = loading
    }

}
