package com.techbeloved.hymnbook.shared.files

import okio.Source

internal expect val assetFileProvider: AssetFileProvider
internal fun interface AssetFileProvider {
    fun get(path: String): Source
}
