package com.techbeloved.hymnbook.shared.songs

import com.techbeloved.hymnbook.shared.model.Lyric
import com.techbeloved.hymnbook.shared.model.SongAuthor
import com.techbeloved.hymnbook.shared.model.SongBookEntry

internal data class SongData(
    val title: String,
    val alternativeTitles: List<String>,
    val lyrics: List<Lyric>,
    val topics: List<String>,
    val authors: List<SongAuthor>,
    val songbookEntries: Set<SongBookEntry>,
)
