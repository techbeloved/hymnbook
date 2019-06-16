package com.techbeloved.hymnbook.playlists

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.DialogCreateNewPlaylistBinding
import com.techbeloved.hymnbook.di.Injection
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*

class CreateNewPlaylistDialogFragment : BottomSheetDialogFragment() {

    private val disposables = CompositeDisposable()

    private lateinit var viewModel: ManagePlaylistViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ManagePlaylistViewModel.Factory(Injection.providePlaylistRepo, Injection.provideSchedulers)
        viewModel = ViewModelProviders.of(this, factory)[ManagePlaylistViewModel::class.java]
    }


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
                val playlistCreate = PlaylistEvent.Create(
                        titleEdittext.text.toString(),
                        descriptionEditText.text?.toString(), Date())
                viewModel.saveNewPlaylist(playlistCreate)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.playlistSaved.observe(viewLifecycleOwner, Observer { savedStatus ->
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