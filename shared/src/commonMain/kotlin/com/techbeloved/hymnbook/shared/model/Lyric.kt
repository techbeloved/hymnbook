package com.techbeloved.hymnbook.shared.model

import kotlinx.serialization.Serializable

@Serializable
public data class Lyric(
    val type: Type,
    val label: String,
    val content: String,
) {
    enum class Type {
        Chorus,
        Verse,
        PreChorus,
        Bridge,
        Intro,
        Ending,
        Other,
    }
}
