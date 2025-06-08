package com.techbeloved.hymnbook.shared.ui.playlist.select

import com.techbeloved.hymnbook.shared.model.playlist.PlaylistItem
import kotlinx.collections.immutable.ImmutableList

internal data class AddSongToPlaylistState(
    val playlists: ImmutableList<PlaylistItem>,
    val songToAdd: Long,
)
