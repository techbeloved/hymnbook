package com.techbeloved.hymnbook.shared.ui.soundfonts

import kotlinx.collections.immutable.ImmutableList

internal data class SoundFontSettingsState(
    val items: ImmutableList<SoundFontItem>,
    val isLoading: Boolean,
)
