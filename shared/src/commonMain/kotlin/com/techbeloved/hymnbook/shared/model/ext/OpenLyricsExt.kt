package com.techbeloved.hymnbook.shared.model.ext

import com.techbeloved.hymnbook.shared.model.Lyric

public fun OpenLyricsSong.Verse.toLyric(): Lyric = Lyric(
    type = Lyric.Type.entries.firstOrNull {
        name.first().equals(it.name.first(), ignoreCase = true)
    } ?: Lyric.Type.Verse,
    label = name,
    content = lines.joinToString(separator = "\n") { it.content.trimIndent() },
)
