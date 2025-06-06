package com.techbeloved.hymnbook.shared.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SongFilter(
    val topics: List<String>,
    val songbooks: List<String>,
    val orderByTitle: Boolean,
) {
    val byTopicsAndSongbooks: Boolean = topics.isNotEmpty() && songbooks.isNotEmpty()
    val byTopicsOnly: Boolean = topics.isNotEmpty() && songbooks.isEmpty()
    val bySongbooks: Boolean = topics.isEmpty() && songbooks.isNotEmpty()
    val none: Boolean = topics.isEmpty() && songbooks.isEmpty()

    companion object {

        val NONE = SongFilter(
            topics = emptyList(),
            songbooks = emptyList(),
            orderByTitle = false,
        )

        fun songbookFilter(songbook: String): SongFilter = SongFilter(
            topics = emptyList(),
            songbooks = listOf(songbook),
            orderByTitle = false,
        )
    }
}
