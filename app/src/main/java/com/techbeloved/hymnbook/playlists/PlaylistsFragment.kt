package com.techbeloved.hymnbook.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentPlaylistsBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.usecases.Lce

class PlaylistsFragment : Fragment() {

    private lateinit var viewModel: PlaylistsViewModel

    private val clickListener: HymnItemModel.ClickListener<HymnItemModel> = object : HymnItemModel.ClickListener<HymnItemModel> {
        override fun onItemClick(item: HymnItemModel) {

        }
    }

    private val playlistsAdapter by lazy { PlaylistsAdapter(clickListener) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = PlaylistsViewModel.Factory(Injection.providePlaylistRepo, Injection.provideSchedulers)
        viewModel = ViewModelProviders.of(this, factory).get(PlaylistsViewModel::class.java)
    }

    private lateinit var binding: FragmentPlaylistsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlists, container, false)


        binding.recyclerviewPlaylists.apply {
            adapter = playlistsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.playlistsData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Lce.Content -> displayContent(it.content)
                is Lce.Loading -> showUiLoading(true)
                is Lce.Error -> showLoadingError(it.error)
            }
        })
    }

    private fun displayContent(content: List<HymnItemModel>) {
        showUiLoading(false)
        playlistsAdapter.submitList(content)
    }

    private fun showLoadingError(error: String) {
        showUiLoading(false)
    }

    private fun showUiLoading(loading: Boolean) {
        binding.progressBarPlaylistsLoading.isVisible = loading
    }

}
