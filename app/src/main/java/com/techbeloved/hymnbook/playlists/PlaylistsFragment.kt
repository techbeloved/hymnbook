package com.techbeloved.hymnbook.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentPlaylistsBinding
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.usecases.Lce
import com.techbeloved.hymnbook.utils.CATEGORY_PLAYLISTS
import com.techbeloved.hymnbook.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModels()

    private val clickListener: HymnItemModel.ClickListener<HymnItemModel> =
        object : HymnItemModel.ClickListener<HymnItemModel> {
            override fun onItemClick(view: View, item: HymnItemModel) {
                findNavController().safeNavigate(
                    PlaylistsFragmentDirections
                        .actionPlaylistsFragmentToHymnListingFragment(
                            categoryId = item.id,
                            category = CATEGORY_PLAYLISTS,
                            title = item.title
                        )
                )
            }

            override fun onOptionsMenuClicked(view: View, item: HymnItemModel) {
                PopupMenu(requireContext(), view).apply {
                    inflate(R.menu.playlists_popup_menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_delete_playlist -> {
                                viewModel.requestPlaylistDelete(item.id)
                                true
                            }
                            R.id.action_playlist_rename -> {
                                showEditPlaylistDialog(item)
                                true
                            }

                            else -> false
                        }
                    }
                    show()
                }

            }
        }

    private val playlistsAdapter by lazy { PlaylistsAdapter(clickListener) }

    private lateinit var binding: FragmentPlaylistsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlists, container, false)

        NavigationUI.setupWithNavController(binding.toolbarPlaylists, findNavController())

        binding.recyclerviewPlaylists.apply {
            adapter = playlistsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
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

    private fun showEditPlaylistDialog(item: HymnItemModel) {
        val createNewPlaylistDialog = CreateNewPlaylistDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(
                    EXTRA_PLAYLIST_UPDATE_EVENT, PlaylistEvent.Update(
                        item.id, item.title, item.subtitle
                            ?: ""
                    )
                )
            }
        }
        createNewPlaylistDialog.show(requireFragmentManager(), null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.playlistsData.observe(viewLifecycleOwner) {
            when (it) {
                is Lce.Content -> displayContent(it.content)
                is Lce.Loading -> showUiLoading(true)
                is Lce.Error -> showLoadingError()
            }
        }

        viewModel.playlistStatus.observe(viewLifecycleOwner, Observer { showSnackbarInfo(it) })
    }

    private fun showSnackbarInfo(status: PlaylistStatus) {
        when (status) {
            PlaylistStatus.Deleted -> {
                Timber.i("Playlist deleted successfully")
            }
            PlaylistStatus.None -> {
            }
            is PlaylistStatus.Error -> {
                Timber.w(status.error)
                Snackbar.make(
                    binding.recyclerviewPlaylists,
                    R.string.error_deleting_playlist,
                    Snackbar.LENGTH_LONG
                ).show()
            }
            PlaylistStatus.DeleteRequested -> Snackbar.make(
                binding.recyclerviewPlaylists,
                R.string.playlist_deleted,
                Snackbar.LENGTH_LONG
            ).apply {
                setAction("Undo") {
                    viewModel.undoDelete()
                }
                show()
            }
        }
    }

    private fun displayContent(content: List<HymnItemModel>) {
        showUiLoading(false)
        playlistsAdapter.submitList(content)
        binding.isEmpty = content.isEmpty()
    }

    private fun showLoadingError() {
        showUiLoading(false)
        binding.isEmpty = false
    }

    private fun showUiLoading(loading: Boolean) {
        binding.progressBarPlaylistsLoading.isVisible = loading
        binding.isEmpty = false
    }

}
