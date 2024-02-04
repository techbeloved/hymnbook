package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.ui.text.AnnotatedString

internal data class SongUiDetail(
    val title: AnnotatedString = AnnotatedString(""),
    val content: AnnotatedString = AnnotatedString(""),
)
