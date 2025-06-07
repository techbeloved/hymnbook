package com.techbeloved.hymnbook.shared.model.playlist

internal data class SongInPlaylist(
    val id: Long,
    val title: String,
    val alternateTitle: String?,
    val playlistId: Long,
)
