package com.techbeloved.hymnbook.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * A [ViewModel] used centrally by the main activity to manage playlists. It handles things such as
 *  playlist creation, deletion, adding of songs to playlist, etc
 */
class ManagePlaylistViewModel(val playlistsRepo: PlaylistsRepo) : ViewModel() {


    class Factory(val playlistsRepo: PlaylistsRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ManagePlaylistViewModel(playlistsRepo) as T
        }
    }
}