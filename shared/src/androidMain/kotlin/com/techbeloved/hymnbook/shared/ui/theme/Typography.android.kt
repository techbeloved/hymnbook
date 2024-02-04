package com.techbeloved.hymnbook.shared.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.techbeloved.hymnbook.shared.R

internal actual val crimsonTextFont: FontFamily by lazy {
    FontFamily(
        Font(R.font.crimson_text_regular, FontWeight.Normal, FontStyle.Normal),
        Font(R.font.crimson_text_bold, FontWeight.Bold, FontStyle.Normal),
        Font(R.font.crimson_text_italic, FontWeight.Normal, FontStyle.Italic),
    )
}
