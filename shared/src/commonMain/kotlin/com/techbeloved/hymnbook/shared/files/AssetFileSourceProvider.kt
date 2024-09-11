package com.techbeloved.hymnbook.shared.files

import okio.Source

internal expect val assetFileSourceProvider: AssetFileSourceProvider
internal fun interface AssetFileSourceProvider {
    fun get(path: String): Source
}
