package com.techbeloved.hymnbook.shared.model.soundfont

import kotlinx.serialization.Serializable

@Serializable
internal data class OnlineSoundFont(
    val name: String,
    val displayName: String,
    val downloadUrl: String,
    val size: String,
    val checksum: String,
    val quality: String,
)
