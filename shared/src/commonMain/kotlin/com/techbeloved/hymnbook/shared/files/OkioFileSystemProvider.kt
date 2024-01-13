package com.techbeloved.hymnbook.shared.files

internal expect val fileSystemProvider: OkioFileSystemProvider
internal fun interface OkioFileSystemProvider {

    fun get(): SharedFileSystem
}
