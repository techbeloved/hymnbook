package com.techbeloved.hymnbook.shared.model

import okio.Path

public data class SheetMusic(
    val songId: Long,
    val filePath: Path,
    val type: Type,
) {
    public enum class Type {
        Pdf,
        Image,
    }
}
