package com.techbeloved.hymnbook.shared.tools

import okio.FileSystem

internal actual val fileSystemProvider: OkioFileSystemProvider = OkioFileSystemProvider { FileSystem.SYSTEM }
