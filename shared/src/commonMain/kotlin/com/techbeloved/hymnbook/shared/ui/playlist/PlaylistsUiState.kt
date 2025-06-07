package com.techbeloved.hymnbook.shared.ui.playlist

import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class PlaylistsUiState(
    val isLoading: Boolean = false,
    val playlists: ImmutableList<PlaylistItem> = persistentListOf(),
) {
    val isEmpty = playlists.isEmpty()
}
