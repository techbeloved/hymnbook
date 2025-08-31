package com.techbeloved.hymnbook.shared.ext

import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

internal actual fun absoluteFileUrl(relativePath: String): Path {
    val defaultDir = NSFileManager.defaultManager.URLsForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomains = NSUserDomainMask,
    ) as List<NSURL>
    return checkNotNull(
        defaultDir.first().URLByAppendingPathComponent(relativePath)?.absoluteURL?.path
    ).toPath()
}
