package com.techbeloved.hymnbook.shared.ui.playlist.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.analytics.TrackAnalyticsEventUseCase
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.playlist.AddSongToPlaylistUseCase
import com.techbeloved.hymnbook.shared.playlist.CreatePlaylistUseCase
import com.techbeloved.hymnbook.shared.playlist.GetPlaylistByIdUseCase
import com.techbeloved.hymnbook.shared.playlist.UpdatePlaylistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal class AddEditPlaylistViewModel @Inject constructor(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val updatePlaylistUseCase: UpdatePlaylistUseCase,
    private val getPlaylistByIdUseCase: GetPlaylistByIdUseCase,
    private val addSongToPlaylistUseCase: AddSongToPlaylistUseCase,
    private val trackAnalyticsUseCase: TrackAnalyticsEventUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<AddEditPlaylistDialog>()

    private val inProgress = MutableStateFlow(false)
    private val playlistSaved = MutableStateFlow<PlaylistSaved?>(null)

    var title by mutableStateOf("")
    var description by mutableStateOf("")

    private val editing = combine(
        snapshotFlow { title },
        snapshotFlow { description },
    ) { name, description ->
        Editing(
            name = name,
            description = description,
        )
    }

    private val oldPlaylist = flow {
        val playlist =
            if (args.playlistId != null) getPlaylistByIdUseCase(args.playlistId) else null
        emit(playlist)
    }

    val state = combine(
        oldPlaylist,
        editing,
        inProgress,
        playlistSaved,
    ) { oldPlaylist, editing, inProgress, playlistSaved ->
        // Prefer the user changed values. Except it is empty, in which case use the old value
        AddEditPlaylistState(
            oldItem = oldPlaylist,
            name = editing.name.ifBlank { oldPlaylist?.name ?: "" },
            description = editing.description.ifBlank { oldPlaylist?.description ?: "" },
            isLoading = inProgress,
            playlistSaved = playlistSaved,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = AddEditPlaylistState(isNewPlaylist = true),
    )

    init {
        viewModelScope.launch {
            val playlist =
                if (args.playlistId != null) getPlaylistByIdUseCase(args.playlistId) else null
            playlist?.let {
                title = it.name
                description = it.description ?: ""
            }
        }
    }

    fun onNameChanged(name: String) {
        if (name.length < NAME_MAX_LENGTH) {
            title = name
        }
    }

    fun onDescriptionChanged(description: String) {
        if (description.length < DESCRIPTION_MAX_LENGTH) {
            this.description = description
        }
    }

    fun onSavePlaylist() {
        inProgress.update { true }
        viewModelScope.launch {
            val currentState = state.value
            val playlistId = if (args.playlistId != null) {
                updatePlaylistUseCase(
                    playlistId = args.playlistId,
                    name = currentState.name,
                    description = currentState.description.ifBlank { null },
                    imageUrl = null,
                )
            } else {
                createPlaylistUseCase(
                    name = currentState.name,
                    description = currentState.description.ifBlank { null },
                    imageUrl = null,
                )
            }
            if (args.songId != null) {
                addSongToPlaylistUseCase(playlistId = playlistId, songId = args.songId)
            }
            inProgress.update { false }
            playlistSaved.update { PlaylistSaved(songAdded = args.songId != null) }
        }
    }

    fun onScreenLoaded() {
        viewModelScope.launch {
            trackAnalyticsUseCase(AddEditPlaylistAnalytics.screenView(isEdit = args.playlistId != null))
        }
    }

    data class Editing(
        val name: String,
        val description: String,
    )

    @Inject
    class Factory(val create: (SavedStateHandle) -> AddEditPlaylistViewModel)

    companion object Companion {
        val Factory = viewModelFactory {
            initializer<AddEditPlaylistViewModel> {
                appComponent.addNewPlaylistViewModelFactory().create(createSavedStateHandle())
            }
        }

        const val DESCRIPTION_MAX_LENGTH = 200
        const val NAME_MAX_LENGTH = 50
    }
}
