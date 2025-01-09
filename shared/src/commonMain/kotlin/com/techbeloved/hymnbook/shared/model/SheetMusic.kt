package com.techbeloved.hymnbook.shared.model

import okio.Path

public data class SheetMusic(
    val songId: Long,
    /**
     * This is a relative path. For example, sheets/music1.pdf
     */
    val relativePath: Path,
    val absolutePath: Path,
    val type: Type,
) {
    public enum class Type {
        Pdf,
        Image,
    }
}
