@file:OptIn(ExperimentalResourceApi::class)

package com.techbeloved.hymnbook.shared.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import hymnbook.shared.generated.resources.Res
import hymnbook.shared.generated.resources.crimson_text_bold
import hymnbook.shared.generated.resources.crimson_text_italic
import hymnbook.shared.generated.resources.crimson_text_regular
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

internal val crimsonText
    @Composable get() = FontFamily(
        Font(Res.font.crimson_text_regular),
        Font(Res.font.crimson_text_bold, weight = FontWeight.Bold),
        Font(Res.font.crimson_text_italic, style = FontStyle.Italic),
    )
