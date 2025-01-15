package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.ui.settings.NowPlayingBottomSettingsState

internal sealed interface DetailBottomSheetState {
    data object Hidden: DetailBottomSheetState

    data class Show(val settingsState: NowPlayingBottomSettingsState): DetailBottomSheetState
}
