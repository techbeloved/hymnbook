package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.preferences.SongPreferences

internal sealed interface DetailBottomSheetState {
    data object Hidden : DetailBottomSheetState

    data class Show(val preferences: SongPreferences) : DetailBottomSheetState
}
