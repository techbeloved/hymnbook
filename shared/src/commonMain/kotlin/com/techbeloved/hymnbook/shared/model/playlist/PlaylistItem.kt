package com.techbeloved.hymnbook.shared.model.playlist

import kotlinx.datetime.Instant

internal data class PlaylistItem(
    val id: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val created: Instant,
    val updated: Instant,
)
