package com.techbeloved.hymnbook.shared.ui.playlist.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.techbeloved.hymnbook.shared.di.appComponent
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
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<AddEditPlaylistDialog>()
    private val playlistName = savedStateHandle.getStateFlow<String?>("playlistName", null)
    private val playlistDescription =
        savedStateHandle.getStateFlow<String?>("playlistDescription", null)

    private val playlistImageUrl = savedStateHandle.getStateFlow<String?>("playlistImageUrl", null)

    private val inProgress = MutableStateFlow(false)
    private val savedPlaylistId = MutableStateFlow<Long?>(null)

    private val editing = combine(
        playlistName,
        playlistDescription,
        playlistImageUrl
    ) { name, description, imageUrl ->
        Editing(
            name = name ?: "",
            description = description ?: "",
            imageUrl = imageUrl,
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
        savedPlaylistId,
    ) { oldPlaylist, editing, inProgress, playlistSaved ->
        // Prefer the user changed values. Except it is empty, in which case use the old value
        AddEditPlaylistState(
            oldItem = oldPlaylist,
            name = editing.name.ifBlank { oldPlaylist?.name ?: "" },
            description = editing.description.ifBlank { oldPlaylist?.description ?: "" },
            imageUrl = editing.imageUrl ?: oldPlaylist?.imageUrl,
            isLoading = inProgress,
            savedPlaylistId = playlistSaved,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = AddEditPlaylistState(isNewPlaylist = true),
    )

    fun onNameChanged(name: String) {
        if (name.length < NAME_MAX_LENGTH) {
            savedStateHandle["playlistName"] = name
        }
    }

    fun onDescriptionChanged(description: String) {
        if (description.length < DESCRIPTION_MAX_LENGTH) {
            savedStateHandle["playlistDescription"] = description
        }
    }

    fun onImageUrlChanged(imageUrl: String?) {
        savedStateHandle["playlistImageUrl"] = imageUrl
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
                    imageUrl = currentState.imageUrl,
                )
            } else {
                createPlaylistUseCase(
                    name = currentState.name,
                    description = currentState.description.ifBlank { null },
                    imageUrl = currentState.imageUrl,
                )
            }
            inProgress.update { false }
            savedPlaylistId.update { playlistId }
        }
    }

    data class Editing(
        val name: String,
        val description: String,
        val imageUrl: String?,
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
