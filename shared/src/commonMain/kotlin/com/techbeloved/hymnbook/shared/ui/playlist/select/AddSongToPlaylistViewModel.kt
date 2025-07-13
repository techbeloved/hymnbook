package com.techbeloved.hymnbook.shared.ui.playlist.select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.analytics.TrackAnalyticsEventUseCase
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem
import com.techbeloved.hymnbook.shared.playlist.AddSongToPlaylistUseCase
import com.techbeloved.hymnbook.shared.playlist.GetPlaylistsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal class AddSongToPlaylistViewModel @Inject constructor(
    private val addSongToPlaylistUseCase: AddSongToPlaylistUseCase,
    private val trackAnalyticsUseCase: TrackAnalyticsEventUseCase,
    getPlaylistsUseCase: GetPlaylistsUseCase,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<AddSongToPlaylistDialog>()

    val state = getPlaylistsUseCase().map { playlists ->
        AddSongToPlaylistState(
            playlists = playlists.toImmutableList(),
            songToAdd = args.songId,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = AddSongToPlaylistState(
            playlists = emptyList<PlaylistItem>().toImmutableList(),
            songToAdd = args.songId,
        )
    )

    fun onSelectPlaylist(playlistId: Long) {
        viewModelScope.launch {
            addSongToPlaylistUseCase(songId = args.songId, playlistId = playlistId)
        }
    }

    fun onScreenLoaded() {
        viewModelScope.launch {
            trackAnalyticsUseCase(AddSongToPlaylistAnalytics.screenView())
        }
    }

    @Inject
    class Factory(val create: (SavedStateHandle) -> AddSongToPlaylistViewModel)

    companion object {

        val Factory = viewModelFactory {
            initializer {
                appComponent.addSongToPlaylistViewModelFactory().create(createSavedStateHandle())
            }
        }
    }
}
