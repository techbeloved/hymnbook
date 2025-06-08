package com.techbeloved.hymnbook.shared.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SongFilter(
    val topics: List<String>,
    val songbooks: List<String>,
    val playlistIds: List<Long>,
    val orderByTitle: Boolean,
) {
    val byTopicsAndSongbooks: Boolean = topics.isNotEmpty() && songbooks.isNotEmpty()
    val byTopicsOnly: Boolean = topics.isNotEmpty() && songbooks.isEmpty()
    val bySongbooks: Boolean = topics.isEmpty() && songbooks.isNotEmpty()
    val byPlaylists: Boolean = playlistIds.isNotEmpty()
    val none: Boolean = topics.isEmpty() && songbooks.isEmpty()

    companion object {

        val NONE = SongFilter(
            topics = emptyList(),
            songbooks = emptyList(),
            playlistIds = emptyList(),
            orderByTitle = false,
        )

        fun songbookFilter(songbook: String, sortByTitle: Boolean = false): SongFilter = SongFilter(
            topics = emptyList(),
            songbooks = listOf(songbook),
            playlistIds = emptyList(),
            orderByTitle = sortByTitle,
        )
    }
}
