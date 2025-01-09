package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.ui.text.AnnotatedString
import com.techbeloved.sheetmusic.SheetMusicItem

internal data class SongUiDetail(
    val title: AnnotatedString = AnnotatedString(""),
    val content: AnnotatedString = AnnotatedString(""),
    val sheetMusic: SheetMusicItem? = null,
    val preferSheetMusic: Boolean = false,
)
