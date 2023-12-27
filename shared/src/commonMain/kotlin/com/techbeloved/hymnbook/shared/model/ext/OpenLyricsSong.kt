package com.techbeloved.hymnbook.shared.model.ext

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlChildrenName
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@SerialName("song")
@XmlSerialName(value = "song")
public data class OpenLyricsSong(
    // Metadata
    val lang: String = "en",
    val chordNotation: String = "english",
    val version: String = "0.9", // open lyrics version
    val createdIn: String? = null,
    val modifiedIn: String? = null,
    val modifiedDate: String? = null,

    val properties: Properties,
    @XmlChildrenName("verse")
    val lyrics: List<Verse>,
) {
    @Serializable
    @SerialName("properties")
    public data class Properties(
        @XmlChildrenName("title")
        val titles: List<Title>,
        @XmlChildrenName("author")
        val authors: List<Author>? = null,
        @XmlChildrenName("songbook")
        val songbooks: List<Songbook>? = null,
        val verseOrder: String? = null,
        val keywords: String? = null,
        @XmlChildrenName("theme")
        val themes: List<Theme>? = null,
        @XmlChildrenName("comment")
        val comments: List<String>? = null,
        val copyright: String? = null,
        val publisher: String? = null,
    )

    @Serializable
    public data class Author(val name: String, val type: String? = null, val comment: String? = null)

    @Serializable
    public data class Title(
        @XmlValue val value: String,
        val lang: String? = null,
        val original: Boolean? = null,
        val translit: String? = null, // transliteration language
    )

    @Serializable
    public data class Songbook(
        val name: String,
        val entry: String? = null,
    )

    @Serializable
    public data class Theme(@XmlValue val name: String, val lang: String? = null)

    @Serializable
    public data class Verse(
        val name: String,
        val lines: List<Lines>,
    )

    @Serializable
    @SerialName("lines")
    public data class Lines(
        @XmlValue val content: String,
        val part: String? = null, // e.g. men, women, alto (etc)
    )
}
