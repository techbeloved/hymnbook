package com.techbeloved.hymnbook.playlists

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Scene
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.textChanges
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.DialogFragmentAddToPlaylistBinding
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*

const val EXTRA_SELECTED_HYMN_ID = "selectedHymnId"

@AndroidEntryPoint
class AddToPlaylistDialogFragment : BottomSheetDialogFragment() {

    private val clickListener: HymnItemModel.ClickListener<HymnItemModel> = object : HymnItemModel.ClickListener<HymnItemModel> {
        override fun onItemClick(view: View, item: HymnItemModel) {
            viewModel.addSelectedHymnToPlaylist(item.id)
        }
    }

    private val disposables = CompositeDisposable()

    private val viewModel: ManagePlaylistViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedHymnId = requireArguments().getInt(EXTRA_SELECTED_HYMN_ID)
        viewModel.updateSelectedHymnId(selectedHymnId)
    }

    private lateinit var playlistAdapter: SimplePlaylistsAdapter
    private lateinit var binding: DialogFragmentAddToPlaylistBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_add_to_playlist, container, false)

        val sceneRoot: ViewGroup = binding.sceneRootAddToPlaylist
        val selectPlaylistScene = Scene.getSceneForLayout(sceneRoot, R.layout.dialog_select_playlist, requireContext())
        val createPlaylistScene = Scene.getSceneForLayout(sceneRoot, R.layout.dialog_create_new_playlist, requireContext())

        val slide = Slide()
        slide.slideEdge = Gravity.RIGHT
        binding.dialogSelectPlaylist.buttonSelectPlaylistAddNew.setOnClickListener { TransitionManager.go(createPlaylistScene, slide) }

        // setup create new playlist screen
        createPlaylistScene.setEnterAction {
            Timber.i("Enter create new playlist")
            val titleEdittext = sceneRoot.findViewById<TextInputEditText>(R.id.edittext_create_new_playlist_title)
            val descriptionEditText = sceneRoot.findViewById<TextInputEditText>(R.id.edittext_create_new_playlist_description)
            val saveButton = sceneRoot.findViewById<Button>(R.id.button_create_new_playlist_save)
            titleEdittext.textChanges().skipInitialValue()
                    .subscribe({ title ->
                        Timber.i("Title: %s", title)
                        if (title.isNotEmpty() && !saveButton.isEnabled) {
                            saveButton.isEnabled = true
                        }
                    }, { Timber.w(it) })
                    .let { disposables.add(it) }
            saveButton.setOnClickListener {
                if (titleEdittext.text != null) {
                    val playlistCreate = PlaylistEvent.Create(
                            titleEdittext.text.toString(),
                            descriptionEditText.text?.toString(), Date())
                    viewModel.saveFavoriteInNewPlaylist(playlistCreate)
                }
            }
        }
        createPlaylistScene.setExitAction {
            if (!disposables.isDisposed) disposables.dispose()
        }

        playlistAdapter = SimplePlaylistsAdapter(clickListener)
        binding.dialogSelectPlaylist.recyclerviewSelectPlaylist.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.playlists.observe(viewLifecycleOwner, { playlists ->
            playlistAdapter.submitList(playlists)
        })
        viewModel.favoriteSaved.observe(viewLifecycleOwner, { saved ->
            showFavoriteSavedSnackbar(saved)
        })
    }

    private fun showFavoriteSavedSnackbar(favoriteStatus: ManagePlaylistViewModel.SaveStatus) {
        when (favoriteStatus) {
            ManagePlaylistViewModel.SaveStatus.Saved -> {
                Snackbar.make(requireView().rootView, R.string.success_adding_to_playlist, Snackbar.LENGTH_SHORT)
                        .show()
            }
            is ManagePlaylistViewModel.SaveStatus.SaveFailed -> {
                if (favoriteStatus.error is SQLiteConstraintException) {
                    Snackbar.make(requireView().rootView, R.string.error_hymn_already_exist, Snackbar.LENGTH_SHORT)
                            .show()
                }
                Timber.w(favoriteStatus.error)
            }
            ManagePlaylistViewModel.SaveStatus.Dismiss -> dismiss()
        }
        Timber.i("Added: %s", favoriteStatus)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
    }
}