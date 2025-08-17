package com.techbeloved.hymnbook.shared.ui.soundfonts

import com.techbeloved.media.download.MediaDownloadState

internal data class SoundFontItem(
    val fileName: String,
    val displayName: String,
    val checksum: String,
    val isDownloaded: Boolean,
    val isPreferred: Boolean,
    val downloadState: MediaDownloadState?,
    val downloadUrl: String,
    val fileSize: String,
)
