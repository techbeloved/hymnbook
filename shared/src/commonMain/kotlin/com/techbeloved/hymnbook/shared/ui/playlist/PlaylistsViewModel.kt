package com.techbeloved.hymnbook.shared.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem
import com.techbeloved.hymnbook.shared.playlist.DeletePlaylistUseCase
import com.techbeloved.hymnbook.shared.playlist.GetPlaylistsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

internal class PlaylistsViewModel @Inject constructor(
    getPlaylistUseCase: GetPlaylistsUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
) : ViewModel() {

    val state = getPlaylistUseCase().map { playlists ->
        PlaylistsUiState(
            isLoading = false,
            playlists = playlists.toImmutableList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlaylistsUiState(isLoading = true),
    )

    fun onDeletePlaylist(playlistItem: PlaylistItem) {
        viewModelScope.launch {
            deletePlaylistUseCase(playlistId = playlistItem.id)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { appComponent.playlistsViewModel() }
        }
    }

}
