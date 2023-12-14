package com.techbeloved.hymnbook.shared.model

data class SongTitle(
    val id: Long,
    val title: String,
    val alternateTitle: String?,
    val songbook: String?,
    val songbookEntry: String?,
)
