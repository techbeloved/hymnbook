package com.techbeloved.hymnbook.shared.ext

import com.techbeloved.hymnbook.shared.files.SharedFileSystem

internal fun SharedFileSystem.tunesDir() = userData / "tunes"

internal fun SharedFileSystem.sheetsDir() = userData / "sheets"
