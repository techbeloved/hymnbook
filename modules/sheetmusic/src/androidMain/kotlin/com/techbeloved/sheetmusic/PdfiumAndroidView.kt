package com.techbeloved.sheetmusic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment

@Composable
internal fun PdfiumAndroidView(
    absolutePath: String,
    modifier: Modifier,
) {

    AndroidFragment<PdfiumFragment>(
        arguments = bundleOf("documentPath" to absolutePath),
        modifier = modifier,
    ) {

    }

}
