package com.techbeloved.hymnbook.shared.tools

import okio.FileSystem
import okio.Path.Companion.toPath

internal actual val assetFileProvider: AssetFileProvider = AssetFileProvider {
    FileSystem.RESOURCES.source(it.toPath())
}
