package com.techbeloved.hymnbook.shared.ui.share

import androidx.compose.runtime.Composable
import com.techbeloved.hymnbook.shared.songshare.ShareAppData

@Composable
internal expect fun NativeShareButton(
    content: @Composable NativeShareButtonOnClick.() -> Unit,
)

internal fun interface NativeShareButtonOnClick {
    fun onClick(shareData: ShareAppData)
}
