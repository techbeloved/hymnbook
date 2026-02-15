package com.techbeloved.hymnbook.shared.ui.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.techbeloved.hymnbook.shared.songshare.ShareAppData

internal expect val ShareIcon: ImageVector

@Composable
internal expect fun NativeShareButton(
    content: @Composable NativeShareButtonOnClick.() -> Unit,
)

internal fun interface NativeShareButtonOnClick {
    fun onClick(shareData: ShareAppData)
}
