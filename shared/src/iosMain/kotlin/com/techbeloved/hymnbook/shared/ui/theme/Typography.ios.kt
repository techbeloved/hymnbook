package com.techbeloved.hymnbook.shared.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@OptIn(ExperimentalResourceApi::class)
internal actual val crimsonTextFont: FontFamily by lazy {
    val regular = runBlocking { resource("res/font/crimson_text_regular.ttf").readBytes() }
    val bold = runBlocking { resource("res/font/crimson_text_bold.ttf").readBytes() }
    val italic = runBlocking { resource("res/font/crimson_text_italic.ttf").readBytes() }
    FontFamily(
        androidx.compose.ui.text.platform.Font(
            "crimson_text_regular",
            regular,
            FontWeight.Normal,
            FontStyle.Normal
        ),
        androidx.compose.ui.text.platform.Font(
            "crimson_text_bold",
            bold,
            FontWeight.Bold,
            FontStyle.Normal
        ),
        androidx.compose.ui.text.platform.Font(
            "crimson_text_italic",
            italic,
            FontWeight.Normal,
            FontStyle.Italic
        ),
    )
}
