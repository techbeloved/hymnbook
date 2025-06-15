package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.SongDetail
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.SongPreferences
import com.techbeloved.sheetmusic.SheetMusicItem

internal data class SongUiDetail(
    val content: SongDetail? = null,
    val sheetMusic: SheetMusicItem? = null,
    val songDisplayMode: SongDisplayMode = SongDisplayMode.Lyrics,
    val fontSizeMultiplier: Float = SongPreferences.DEFAULT_FONT_SIZE,
)
