package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.di.AndroidInjector
import okio.FileSystem
import okio.Path.Companion.toOkioPath

internal actual val defaultOkioFileSystemProvider: OkioFileSystemProvider = AndroidFileSystemProvider()

private class AndroidFileSystemProvider : OkioFileSystemProvider {

    val sharedFileSystem by lazy {
        SharedFileSystem(
            fileSystem = FileSystem.SYSTEM,
            tempDir = AndroidInjector.application.cacheDir.toOkioPath(),
            userData = AndroidInjector.application.filesDir.toOkioPath(),
        )
    }

    override fun get(): SharedFileSystem = sharedFileSystem

}
