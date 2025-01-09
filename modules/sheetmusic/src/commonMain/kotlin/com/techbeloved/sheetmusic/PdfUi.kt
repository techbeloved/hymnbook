package com.techbeloved.sheetmusic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun PdfUi(
    relativePath: String,
    absolutePath: String,
    modifier: Modifier = Modifier,
)
