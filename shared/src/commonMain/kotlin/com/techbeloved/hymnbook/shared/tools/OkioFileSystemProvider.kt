package com.techbeloved.hymnbook.shared.tools

import okio.FileSystem

internal expect val fileSystemProvider: OkioFileSystemProvider
internal fun interface OkioFileSystemProvider {

    fun get(): FileSystem
}
