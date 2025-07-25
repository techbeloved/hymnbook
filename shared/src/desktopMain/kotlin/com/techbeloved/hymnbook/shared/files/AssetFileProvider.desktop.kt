@file:OptIn(ExperimentalResourceApi::class)

package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.generated.Res
import okio.FileSystem
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal actual val assetFileSourceProvider: AssetFileSourceProvider = AssetFileSourceProvider {
    val resourcePath = getResourcePath(it)
    FileSystem.RESOURCES.source(resourcePath.toPath())
}

@OptIn(ExperimentalResourceApi::class)
internal fun getResourcePath(assetFile: String) = Res.getUri(assetFile).split("!").last()
