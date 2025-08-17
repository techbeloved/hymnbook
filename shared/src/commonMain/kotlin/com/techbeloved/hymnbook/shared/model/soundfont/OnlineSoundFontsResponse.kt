package com.techbeloved.hymnbook.shared.model.soundfont

import kotlinx.serialization.Serializable

@Serializable
internal data class OnlineSoundFontsResponse(
    val title: String,
    val soundfonts: List<OnlineSoundFont>,
)
