package com.techbeloved.hymnbook.shared.model.soundfont

import com.techbeloved.hymnbook.shared.model.file.FileHash
import kotlinx.datetime.LocalDateTime

internal data class SavedSoundFont(
    val displayName: String,
    val downloadedDate: LocalDateTime,
    val fileSize: String,
    val fileHash: FileHash,
)
