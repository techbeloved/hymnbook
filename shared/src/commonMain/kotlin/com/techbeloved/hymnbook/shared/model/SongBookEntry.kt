package com.techbeloved.hymnbook.shared.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SongBookEntry(val songbook: String, val entry: String)
