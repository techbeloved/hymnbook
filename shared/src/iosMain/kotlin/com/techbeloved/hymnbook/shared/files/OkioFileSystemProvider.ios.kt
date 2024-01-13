package com.techbeloved.hymnbook.shared.files

import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDomainMask

internal actual val fileSystemProvider: OkioFileSystemProvider = IosFileSystemProvider()

private class IosFileSystemProvider : OkioFileSystemProvider {
    val sharedFileSystem by lazy {
        val appDirectories: List<String> = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
            expandTilde = true
        ) as List<String>
        SharedFileSystem(
            fileSystem = FileSystem.SYSTEM,
            tempDir = NSTemporaryDirectory().toPath(),
            userData = appDirectories.first().toPath(),
        )
    }

    override fun get(): SharedFileSystem = sharedFileSystem
}
