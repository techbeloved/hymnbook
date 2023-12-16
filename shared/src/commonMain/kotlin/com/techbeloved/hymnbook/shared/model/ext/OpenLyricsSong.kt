package com.techbeloved.hymnbook.shared.model.ext

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class OpenLyricsSong(
    val metadata: Metadata,
    val properties: Properties,
    val lyrics: List<Verse>,
) {
    @Serializable
    public data class Properties(
        val titles: List<Title>,
        val authors: List<Author>? = null,
        val songbooks: List<Songbook>? = null,
        val verseOrder: String? = null,
        val keywords: List<String>? = null,
        val themes: List<Theme>? = null,
        val comments: List<String>? = null,
        val copyright: String? = null,
        val publisher: String? = null,
    )

    @Serializable
    public data class Author(val name: String, val type: String? = null, val comment: String? = null)

    @Serializable
    public data class Title(
        val name: String,
        val lang: String? = null,
        val original: Boolean? = null,
        val translit: String? = null, // transliteration language
    )

    @Serializable
    public data class Metadata(
        val lang: String = "en",
        val chordNotation: String = "english",
        val version: String = "0.9", // open lyrics version
        val createdIn: String? = null,
        val modifiedIn: String? = null,
        val modifiedDate: Instant? = null,
    )

    @Serializable
    public data class Songbook(
        val name: String,
        val entry: String? = null,
    )

    @Serializable
    public data class Theme(val name: String, val lang: String? = null)

    @Serializable
    public data class Verse(
        val name: String,
        val lines: List<Line>,
    )

    @Serializable
    public data class Line(
        val content: String,
        val part: String? = null, // e.g. men, women, alto (etc)
    )
}
