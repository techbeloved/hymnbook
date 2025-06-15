package com.techbeloved.hymnbook.shared.model.ext

import com.techbeloved.hymnbook.SongDetail
import com.techbeloved.hymnbook.shared.model.Lyric
import com.techbeloved.hymnbook.shared.model.SongAuthor
import com.techbeloved.hymnbook.shared.model.SongBookEntry

internal fun String.formattedSongbookEntries() = split("::").map {
    val (songbook, entry) = it.split("||")
    SongBookEntry(songbook, entry)
}.toSet()

internal fun SongDetail.songbookEntries() =
    songbookEntries?.formattedSongbookEntries() ?: emptySet()

internal fun SongDetail.authors(): List<SongAuthor> = authors?.let { authorEntry ->
    authorEntry.split("::").map {
        val (name, type, comment) = it.split("||")
        SongAuthor(name, type, comment)
    }
} ?: emptyList()

internal fun SongDetail.topics(): List<String> = topics?.split("::") ?: emptyList()

internal fun SongDetail.lyricsByVerseOrder(): List<Lyric> {
    return verse_order?.let { order ->
        val verseOrder = order.trim().split(" ")
        val labelToLyricsMap = lyrics.associateBy { (it.label ?: it.type.name) }
        verseOrder.mapNotNull(labelToLyricsMap::get)
    } ?: lyrics
}
