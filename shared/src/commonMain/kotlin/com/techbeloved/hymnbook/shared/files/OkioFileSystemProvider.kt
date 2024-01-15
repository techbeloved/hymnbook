package com.techbeloved.hymnbook.shared.files

internal expect val defaultOkioFileSystemProvider: OkioFileSystemProvider
internal fun interface OkioFileSystemProvider {

    fun get(): SharedFileSystem
}
