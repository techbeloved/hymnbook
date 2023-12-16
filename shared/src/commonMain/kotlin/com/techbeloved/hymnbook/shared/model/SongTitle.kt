package com.techbeloved.hymnbook.shared.model

public data class SongTitle(
    val id: Long,
    val title: String,
    val alternateTitle: String?,
    val songbook: String?,
    val songbookEntry: String?,
)
