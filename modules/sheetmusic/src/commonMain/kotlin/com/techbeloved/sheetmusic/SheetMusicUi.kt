package com.techbeloved.sheetmusic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage

@Composable
public fun SheetMusicUi(
    sheetMusicItem: SheetMusicItem,
    modifier: Modifier = Modifier,
) {
    when (sheetMusicItem.type) {
        SheetMusicType.Pdf -> PdfUi(
            relativePath = sheetMusicItem.relativeUri,
            absolutePath = sheetMusicItem.absoluteUri,
            modifier = modifier
        )

        SheetMusicType.Image -> {
            AsyncImage(
                model = sheetMusicItem.absoluteUri,
                contentDescription = null,
                modifier = modifier,
            )
        }
    }
}
