package com.techbeloved.hymnbook.shared.tools

import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSBundle

internal actual val assetFileProvider: AssetFileProvider = AssetFileProvider {
    val assetFile = NSBundle.mainBundle.resourcePath + "/compose-resources/" + it
    FileSystem.SYSTEM.source(assetFile.toPath())
}
