package com.techbeloved.hymnbook.shared.ui.playlist.add

import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem

internal data class AddEditPlaylistState(
    val oldItem: PlaylistItem? = null,
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val isNewPlaylist: Boolean = oldItem == null,
    val isLoading: Boolean = false,
    val playlistSaved: PlaylistSaved? = null,
) {
    val isModified: Boolean = (name.isNotBlank()) && (oldItem?.name != name
            || oldItem.description != description
            || oldItem.imageUrl != imageUrl)

}

internal data class PlaylistSaved(
    val songAdded: Boolean = false,
)
