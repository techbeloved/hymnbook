package com.techbeloved.hymnbook.shared.ui.settings

internal sealed interface NowPlayingBottomSettingsState {
    data object Default : NowPlayingBottomSettingsState
}
