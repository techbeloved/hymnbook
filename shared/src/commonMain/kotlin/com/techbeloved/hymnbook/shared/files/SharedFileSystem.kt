package com.techbeloved.hymnbook.shared.files

import okio.FileSystem
import okio.Path

internal class SharedFileSystem(
    val fileSystem: FileSystem,
    val tempDir: Path,
    val userData: Path,
)
