package com.techbeloved.hymnbook.shared.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

internal actual val crimsonTextFont: FontFamily by lazy {
    FontFamily(
        androidx.compose.ui.text.platform.Font(
            "res/font/crimson_text_regular.ttf",
            FontWeight.Normal,
            FontStyle.Normal
        ),
        androidx.compose.ui.text.platform.Font(
            "res/font/crimson_text_bold.ttf",
            FontWeight.Bold,
            FontStyle.Normal
        ),
        androidx.compose.ui.text.platform.Font(
            "res/font/crimson_text_italic.ttf",
            FontWeight.Normal,
            FontStyle.Italic
        ),
    )
}