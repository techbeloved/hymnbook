package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.preferences.SongPreferences
import com.techbeloved.hymnbook.shared.songshare.ShareAppData

internal sealed interface DetailBottomSheetState {
    data object Hidden : DetailBottomSheetState

    data class Show(
        val preferences: SongPreferences,
        val shareAppData: ShareAppData,
    ) : DetailBottomSheetState
}
