package com.techbeloved.hymnbook.shared.songshare

internal data class SongShareData(
    val songbook: String?,
    val songEntry: String?,
    val songQuery: String?,
) {
    val isValidQuery = (!songEntry.isNullOrBlank() && !songbook.isNullOrBlank()) || !songQuery.isNullOrBlank()
}
