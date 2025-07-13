package com.techbeloved.hymnbook.shared.model.jsonimport

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class JsonSongbook(
    val metadata: JsonSongbookMetadata,
    val topics: List<JsonSongTopic>,
    @SerialName("songs")
    val songs: List<JsonSongLyric>,
)

@Serializable
internal data class JsonSongLyric(
    val title: String,
    @SerialName("num")
    val number: Int,
    val verses: List<String>,
    val chorus: String? = null,
    val attribution: JsonSongAttribution? = null,
    @SerialName("topic")
    val topicId: Int,
)

@Serializable
internal data class JsonSongAttribution(
    val credits: String?,
    @SerialName("music_by")
    val musicBy: String?,
    @SerialName("lyrics_by")
    val lyricsBy: String?,
)

@Serializable
internal data class JsonSongTopic(
    val topic: String,
    val id: Int,
)

@Serializable
internal data class JsonSongbookMetadata(
    val title: String,
    @SerialName("longer_title")
    val longerTitle: String,
    val publisher: String,
    @SerialName("publish_date")
    val publishDate: String,
    val revision: String,
    @SerialName("sheet_music")
    val sheetMusicArchive: String,
    @SerialName("sheet_music_prefix")
    val sheetMusicPrefix: String,
    @SerialName("tunes")
    val tunesArchive: String,
    @SerialName("tunes_prefix")
    val tunesPrefix: String,
)
