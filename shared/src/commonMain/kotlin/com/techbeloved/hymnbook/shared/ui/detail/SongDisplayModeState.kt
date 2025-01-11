package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.model.SongDisplayMode

internal data class SongDisplayModeState(
    val displayMode: SongDisplayMode,
    val isEnabled: Boolean,
    val text: String,
)
