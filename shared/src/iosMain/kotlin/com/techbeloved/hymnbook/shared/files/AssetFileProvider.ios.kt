package com.techbeloved.hymnbook.shared.files

import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSBundle

internal actual val assetFileProvider: AssetFileProvider = AssetFileProvider {
    FileSystem.SYSTEM.source(getAssetFilePath(it).toPath())
}

internal fun getAssetFilePath(path: String): String = NSBundle.mainBundle.resourcePath + "/compose-resources/" + path
