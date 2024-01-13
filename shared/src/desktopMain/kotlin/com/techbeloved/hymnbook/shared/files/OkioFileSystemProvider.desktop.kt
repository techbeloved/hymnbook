package com.techbeloved.hymnbook.shared.files

import okio.FileSystem
import okio.Path.Companion.toPath

internal actual val fileSystemProvider: OkioFileSystemProvider = DesktopFileSystemProvider()

private class DesktopFileSystemProvider : OkioFileSystemProvider {
    val sharedFileSystem by lazy {
        SharedFileSystem(
            fileSystem = FileSystem.SYSTEM,
            tempDir = ".tmp".toPath().also {
                FileSystem.SYSTEM.createDirectory(it)
            },
            userData = ".appdata".toPath().also {
                FileSystem.SYSTEM.createDirectory(it)
            },
        )
    }

    override fun get(): SharedFileSystem = sharedFileSystem

}
