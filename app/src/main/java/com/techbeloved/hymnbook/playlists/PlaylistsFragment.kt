package com.techbeloved.hymnbook.playlists

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentPlaylistsBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.AUTHORITY
import com.techbeloved.hymnbook.utils.CATEGORY_PLAYLISTS
import com.techbeloved.hymnbook.utils.SCHEME_NORMAL
import timber.log.Timber

class PlaylistsFragment : Fragment() {

    private lateinit var viewModel: PlaylistsViewModel

    private val clickListener: HymnItemModel.ClickListener<HymnItemModel> =
            object : HymnItemModel.ClickListener<HymnItemModel> {
                override fun onItemClick(view: View, item: HymnItemModel) {
                    val navUri = Uri.Builder()
                            .authority(AUTHORITY)
                            .scheme(SCHEME_NORMAL)
                            .appendEncodedPath(CATEGORY_PLAYLISTS)
                            .appendEncodedPath(item.id.toString())
                            .build()
                    Timber.i("navUri: %s", navUri)
                    findNavController().navigate(PlaylistsFragmentDirections
                            .actionPlaylistsFragmentToHymnListingFragment(item.title, navUri.toString()))
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

        NavigationUI.setupWithNavController(binding.toolbarPlaylists, findNavController())

        binding.recyclerviewPlaylists.apply {
            adapter = playlistsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.toolbarPlaylists.inflateMenu(R.menu.playlists)
        binding.toolbarPlaylists.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_create_new_playlist -> {
                    showCreateNewPlaylistDialog()
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    private fun showCreateNewPlaylistDialog() {
        val createNewPlaylistDialog = CreateNewPlaylistDialogFragment()
        createNewPlaylistDialog.show(requireFragmentManager(), null)
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
