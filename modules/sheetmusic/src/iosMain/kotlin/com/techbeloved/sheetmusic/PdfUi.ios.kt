package com.techbeloved.sheetmusic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.PDFKit.PDFDocument
import platform.PDFKit.PDFView

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun PdfUi(
    relativePath: String,
    absolutePath: String,
    modifier: Modifier,
) {
    val documentNsUrl = remember(relativePath) {
        val defaultDir = NSFileManager.defaultManager.URLsForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomains = NSUserDomainMask,
        ) as List<NSURL>
        defaultDir.first().URLByAppendingPathComponent(relativePath)
    }
    if (documentNsUrl != null) {
        val backgroundColor = MaterialTheme.colorScheme.surface

        UIKitView(
            factory = {
                val pdfDocument = PDFDocument(documentNsUrl)
                PDFView().apply {
                    setFrame(CGRectMake(0.0, 0.0, 0.0, 0.0))
                    setDocument(pdfDocument)
                    setAutoScales(true)
                    setUserInteractionEnabled(true)
                    setBackgroundColor(backgroundColor.toUiColor())
                    setDisplaysPageBreaks(false)
                }
            },
            modifier = modifier,
        )
    }
}
