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
    } ?: buildList {
        val lyricsByType = lyrics.groupBy { it.type }
        lyricsByType[Lyric.Type.Intro]?.forEach { add(it) }
        lyricsByType[Lyric.Type.Verse]?.forEach {
            add(it)
            lyricsByType[Lyric.Type.PreChorus]?.forEach { add(it) }
            lyricsByType[Lyric.Type.Chorus]?.forEach { add(it) }
        }
        lyricsByType[Lyric.Type.Bridge]?.forEach { add(it) }
        lyricsByType[Lyric.Type.Ending]?.forEach { add(it) }
    }
}

/**
 * Returns a list of lyrics with chorus and pre-chorus after the first verse and occurs only once.
 * Compare to [lyricsByVerseOrder], the chorus is not repeated after every verse.
 */
internal fun SongDetail.lyricsCompact(): List<Lyric> = buildList {
    val lyricsByType = lyrics.groupBy { it.type }
    lyricsByType[Lyric.Type.Intro]?.forEach { add(it) }
    lyricsByType[Lyric.Type.Verse]?.sortedBy { it.label }?. forEachIndexed { index, lyric ->
        add(lyric)
        if (index == 0) {
            lyricsByType[Lyric.Type.PreChorus]?.forEach { add(it) }
            lyricsByType[Lyric.Type.Chorus]?.forEach { add(it) }
        }
    }
    lyricsByType[Lyric.Type.Bridge]?.forEach { add(it) }
    lyricsByType[Lyric.Type.Ending]?.forEach { add(it) }
}
