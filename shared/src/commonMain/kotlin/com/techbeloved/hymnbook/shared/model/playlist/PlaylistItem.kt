package com.techbeloved.hymnbook.shared.model.playlist

import kotlin.time.Instant

internal data class PlaylistItem(
    val id: Long,
    val name: String,
    val description: String?,
    val songCount: Long,
    val imageUrl: String?,
    val created: Instant,
    val updated: Instant,
)
