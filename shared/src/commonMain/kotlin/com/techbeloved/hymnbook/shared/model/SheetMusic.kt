package com.techbeloved.hymnbook.shared.model

import okio.Path

data class SheetMusic(
    val songId: Long,
    val filePath: Path,
    val type: Type,
) {
    enum class Type {
        Pdf,
        Image,
    }
}
