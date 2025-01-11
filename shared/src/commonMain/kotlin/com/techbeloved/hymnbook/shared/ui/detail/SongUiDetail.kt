package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.ui.text.AnnotatedString
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.sheetmusic.SheetMusicItem

internal data class SongUiDetail(
    val title: AnnotatedString = AnnotatedString(""),
    val content: AnnotatedString = AnnotatedString(""),
    val sheetMusic: SheetMusicItem? = null,
    val songDisplayMode: SongDisplayMode = SongDisplayMode.Lyrics,
)
