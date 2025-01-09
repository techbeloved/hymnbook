package com.techbeloved.sheetmusic

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import java.io.File

@Composable
internal actual fun PdfUi(
    relativePath: String,
    absolutePath: String,
    modifier: Modifier,
) {
    val isDarkMode = isSystemInDarkTheme()
    AndroidView(
        modifier = modifier,
        factory = {
            PDFView(it, null).apply {

            }
        },
        update = {
            it.fromFile(File(absolutePath))
                .onError { error ->
                    println(error)
                }
                .onPageError { _, t ->
                    t.printStackTrace()
                }
                .pageSnap(true)
                .pageFitPolicy(FitPolicy.WIDTH)
                .autoSpacing(false)
                .pageSnap(true)
                .enableAntialiasing(true)
                .nightMode(isDarkMode)
                .enableDoubletap(true)
                .load()
        }
    )
}
