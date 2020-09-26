package com.techbeloved.hymnbook.playlists

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.DialogCreateNewPlaylistBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*

const val EXTRA_PLAYLIST_UPDATE_EVENT = "extraPlaylistRequest"

/**
 * Bottomsheet dialog used for creating and editing playlist. Unfortunately, the name says create new, but it does edit existing playlist as well
 */
@AndroidEntryPoint
class CreateNewPlaylistDialogFragment : BottomSheetDialogFragment() {

    private val disposables = CompositeDisposable()

    private val viewModel: ManagePlaylistViewModel by viewModels()

    private lateinit var binding: DialogCreateNewPlaylistBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_new_playlist, container, false)

        Timber.i("Enter create new playlist")
        val titleEdittext = binding.edittextCreateNewPlaylistTitle
        val descriptionEditText = binding.edittextCreateNewPlaylistDescription
        val saveButton = binding.buttonCreateNewPlaylistSave
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
                if (!viewModel.editing) {
                    val playlistCreate = PlaylistEvent.Create(
                            titleEdittext.text.toString(),
                            descriptionEditText.text?.toString(), Date())
                    viewModel.saveNewPlaylist(playlistCreate)
                } else {
                    val playlistUpdate = PlaylistEvent.Update(
                            viewModel.editPlaylistId,
                            titleEdittext.text.toString(),
                            descriptionEditText.text.toString()
                    )
                    viewModel.savePlaylist(playlistUpdate)
                }
            }
        }
        if (arguments != null) {
            val playlistUpdateEvent = requireArguments().getParcelable<PlaylistEvent.Update>(EXTRA_PLAYLIST_UPDATE_EVENT)
            if (playlistUpdateEvent != null) {
                titleEdittext.setText(playlistUpdateEvent.title)
                descriptionEditText.setText(playlistUpdateEvent.description)
                viewModel.setPlaylistId(playlistUpdateEvent.id)
                viewModel.setEditing(true)
                binding.textviewCreateNewPlaylistHeader.setText(R.string.edit_playlist)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.playlistSaved.observe(viewLifecycleOwner, { savedStatus ->
            showFavoriteSavedSnackbar(savedStatus)
        })
    }

    private fun showFavoriteSavedSnackbar(saveStatus: ManagePlaylistViewModel.SaveStatus) {
        when (saveStatus) {
            ManagePlaylistViewModel.SaveStatus.Saved -> {
                Snackbar.make(requireView().rootView, R.string.success_creating_playlist, Snackbar.LENGTH_SHORT)
                        .show()
            }
            is ManagePlaylistViewModel.SaveStatus.SaveFailed -> {
                if (saveStatus.error is SQLiteConstraintException) {
                    Snackbar.make(requireView().rootView, R.string.error_playlist_already_exist, Snackbar.LENGTH_SHORT)
                            .show()
                }
                Timber.w(saveStatus.error)
            }
            ManagePlaylistViewModel.SaveStatus.Dismiss -> dismiss()
        }
        Timber.i("Added: %s", saveStatus)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
    }
}