package com.techbeloved.hymnbook.shared.ext

import com.techbeloved.hymnbook.shared.files.SharedFileSystem
import okio.Path

internal fun SharedFileSystem.tunesDir() = userData / "tunes"

internal fun SharedFileSystem.sheetsDir() = userData / "sheets"

internal expect fun absoluteFileUrl(relativePath: String): Path
