package com.techbeloved.hymnbook.shared.ui.detail

import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.SongPreferences
import com.techbeloved.hymnbook.shared.songs.SongData
import com.techbeloved.sheetmusic.SheetMusicItem

internal data class SongUiDetail(
    val content: SongData? = null,
    val sheetMusic: SheetMusicItem? = null,
    val songDisplayMode: SongDisplayMode = SongDisplayMode.Lyrics,
    val fontSizeMultiplier: Float = SongPreferences.DEFAULT_FONT_SIZE,
)
