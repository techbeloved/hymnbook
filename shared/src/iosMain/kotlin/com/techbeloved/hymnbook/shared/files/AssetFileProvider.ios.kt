package com.techbeloved.hymnbook.shared.files

import okio.FileSystem
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal actual val assetFileSourceProvider: AssetFileSourceProvider = AssetFileSourceProvider {
    FileSystem.SYSTEM.source(getAssetFilePath(it).toPath())
}

@OptIn(ExperimentalResourceApi::class)
internal fun getAssetFilePath(path: String): String =
    // getUri appends a file scheme which the okio FileSystem does not understand
    path.removePrefix("file://")
