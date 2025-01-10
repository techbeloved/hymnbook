package com.techbeloved.media

import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

internal fun getNSURLFromRelativePath(relativePath: String): NSURL? {
    val defaultDir = NSFileManager.defaultManager.URLsForDirectory(NSApplicationSupportDirectory, NSUserDomainMask) as List<NSURL>
    return defaultDir.first().URLByAppendingPathComponent(relativePath)
}
